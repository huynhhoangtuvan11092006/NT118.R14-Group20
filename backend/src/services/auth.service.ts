import * as argon2 from "argon2";
import { Prisma } from "@prisma/client";
import { prisma } from "../config/database.js";
import { HttpError } from "../lib/http-error.js";
import {
  ACCESS_TTL_SECONDS, REFRESH_TTL_SECONDS,
  createRefreshId, hashRefreshToken, signAccessToken,
  signRefreshToken, verifyRefreshToken,
} from "../lib/jwt.js";
import type { z } from "zod";
import type { forgotPasswordSchema, googleAuthSchema, loginSchema, refreshSchema, registerSchema, resetPasswordSchema } from "../validators/auth.validator.js";
import { sendOtpEmail } from "./mail.service.js";
import { OAuth2Client } from "google-auth-library";
import { env } from "../config/env.js";

// Only explicitly selected public properties are returned to clients.
export const publicUserSelect = {
  id: true, email: true, fullName: true, avatarUrl: true,
  currentBand: true, targetBand: true, expectedExamDate: true,
  dailyStudyMinutes: true, createdAt: true, updatedAt: true,
} satisfies Prisma.UserSelect;

export type PublicUserRow = Prisma.UserGetPayload<{ select: typeof publicUserSelect }>;

export function presentUser(row: PublicUserRow) {
  return {
    ...row,
    currentBand: row.currentBand?.toNumber() ?? null,
    targetBand: row.targetBand?.toNumber() ?? null,
    expectedExamDate: row.expectedExamDate?.toISOString().slice(0, 10) ?? null,
  };
}

async function issueTokens(userId: string) {
  const id = createRefreshId();
  const refreshToken = signRefreshToken(userId, id);
  await prisma.refreshToken.create({
    data: {
      id, userId, tokenHash: hashRefreshToken(refreshToken),
      expiresAt: new Date(Date.now() + REFRESH_TTL_SECONDS * 1000),
    },
  });
  return {
    accessToken: signAccessToken(userId), refreshToken,
    tokenType: "Bearer" as const, expiresIn: ACCESS_TTL_SECONDS,
  };
}

export async function register(input: z.infer<typeof registerSchema>) {
  const passwordHash = await argon2.hash(input.password, { type: argon2.argon2id });
  let user: PublicUserRow;
  try {
    user = await prisma.user.create({
      data: { email: input.email, fullName: input.fullName, passwordHash },
      select: publicUserSelect,
    });
  } catch (error: unknown) {
    if (error instanceof Prisma.PrismaClientKnownRequestError && error.code === "P2002") {
      throw new HttpError(409, "EMAIL_ALREADY_EXISTS", "Email already registered");
    }
    throw error;
  }
  return { user: presentUser(user), ...await issueTokens(user.id) };
}

export async function login(input: z.infer<typeof loginSchema>) {
  const user = await prisma.user.findUnique({ where: { email: input.email } });
  // Same response for unknown email, wrong password, and social-only account.
  if (!user?.passwordHash || !(await argon2.verify(user.passwordHash, input.password))) {
    throw new HttpError(401, "INVALID_CREDENTIALS", "Invalid email or password");
  }
  const publicUser = await prisma.user.findUniqueOrThrow({ where: { id: user.id }, select: publicUserSelect });
  return { user: presentUser(publicUser), ...await issueTokens(user.id) };
}

export async function refresh(refreshToken: string) {
  const { userId, jti } = verifyRefreshToken(refreshToken);
  if (!jti) throw new HttpError(401, "UNAUTHORIZED", "Invalid refresh token");
  const hash = hashRefreshToken(refreshToken);
  const newId = createRefreshId();
  const nextRefresh = signRefreshToken(userId, newId);
  const now = new Date();
  const result = await prisma.$transaction(async (tx) => {
    const changed = await tx.refreshToken.updateMany({
      where: { id: jti, userId, tokenHash: hash, revokedAt: null, expiresAt: { gt: now } },
      data: { revokedAt: now },
    });
    if (changed.count !== 1) throw new HttpError(401, "UNAUTHORIZED", "Invalid or reused refresh token");
    await tx.refreshToken.create({
      data: {
        id: newId, userId, tokenHash: hashRefreshToken(nextRefresh),
        expiresAt: new Date(Date.now() + REFRESH_TTL_SECONDS * 1000),
      },
    });
    return true;
  });
  if (!result) throw new HttpError(401, "UNAUTHORIZED", "Invalid refresh token");
  return {
    accessToken: signAccessToken(userId), refreshToken: nextRefresh,
    tokenType: "Bearer" as const, expiresIn: ACCESS_TTL_SECONDS,
  };
}

export async function logout(refreshToken: string) {
  const { userId, jti } = verifyRefreshToken(refreshToken);
  if (!jti) throw new HttpError(401, "UNAUTHORIZED", "Invalid refresh token");
  const changed = await prisma.refreshToken.updateMany({
    where: {
      id: jti, userId, tokenHash: hashRefreshToken(refreshToken),
      revokedAt: null, expiresAt: { gt: new Date() },
    },
    data: { revokedAt: new Date() },
  });
  if (changed.count !== 1) throw new HttpError(401, "UNAUTHORIZED", "Invalid or revoked refresh token");
}

export async function forgotPassword(input: z.infer<typeof forgotPasswordSchema>) {
  const user = await prisma.user.findUnique({ where: { email: input.email } });
  if (!user) {
    throw new HttpError(404, "USER_NOT_FOUND", "Không tìm thấy tài khoản với email này");
  }

  // Generate 6-digit OTP
  const otp = Math.floor(100000 + Math.random() * 900000).toString();
  const expiresAt = new Date(Date.now() + 10 * 60 * 1000); // 10 minutes

  // Invalidate any active, unused OTPs for this email
  await prisma.passwordReset.updateMany({
    where: { email: input.email, isUsed: false },
    data: { isUsed: true },
  });

  // Create new password reset record
  await prisma.passwordReset.create({
    data: {
      email: input.email,
      otpCode: otp,
      expiresAt,
      isUsed: false,
    },
  });

  // Dispatch real email (or console log if SMTP not set)
  await sendOtpEmail(input.email, otp);

  return {
    message: "Mã xác nhận OTP đã được gửi đến email của bạn.",
    devOtp: process.env.NODE_ENV !== "production" ? otp : undefined,
  };
}

export async function resetPassword(input: z.infer<typeof resetPasswordSchema>) {
  const user = await prisma.user.findUnique({ where: { email: input.email } });
  if (!user) {
    throw new HttpError(404, "USER_NOT_FOUND", "Không tìm thấy tài khoản với email này");
  }

  const now = new Date();
  const resetRecord = await prisma.passwordReset.findFirst({
    where: {
      email: input.email,
      otpCode: input.otp,
      isUsed: false,
      expiresAt: { gt: now },
    },
    orderBy: { createdAt: "desc" },
  });

  if (!resetRecord) {
    throw new HttpError(400, "INVALID_OTP", "Mã xác nhận không chính xác hoặc đã hết hạn");
  }

  const passwordHash = await argon2.hash(input.newPassword, { type: argon2.argon2id });

  await prisma.$transaction(async (tx) => {
    // 1. Mark OTP as used
    await tx.passwordReset.update({
      where: { id: resetRecord.id },
      data: { isUsed: true },
    });

    // 2. Update user's password
    await tx.user.update({
      where: { id: user.id },
      data: { passwordHash },
    });

    // 3. Revoke all active refresh tokens for security
    await tx.refreshToken.updateMany({
      where: { userId: user.id, revokedAt: null },
      data: { revokedAt: now },
    });
  });

  return {
    message: "Đặt lại mật khẩu thành công. Vui lòng đăng nhập lại.",
  };
}

export async function googleAuth(input: z.infer<typeof googleAuthSchema>) {
  let email: string | undefined;
  let fullName = "Google User";
  let avatarUrl: string | null = null;

  if (env.GOOGLE_CLIENT_ID) {
    try {
      const client = new OAuth2Client(env.GOOGLE_CLIENT_ID);
      const ticket = await client.verifyIdToken({
        idToken: input.idToken,
        audience: env.GOOGLE_CLIENT_ID,
      });
      const payload = ticket.getPayload();
      if (!payload || !payload.email) {
        throw new HttpError(401, "INVALID_TOKEN", "Không thể lấy thông tin email từ Google");
      }
      const emailVal = payload.email.toLowerCase();
      email = emailVal;
      fullName = payload.name ?? emailVal.split("@")[0] ?? "Google User";
      avatarUrl = payload.picture ?? null;
    } catch (err: unknown) {
      if (err instanceof HttpError) throw err;
      throw new HttpError(401, "INVALID_TOKEN", "Mã xác thực Google không hợp lệ hoặc đã hết hạn");
    }
  } else {
    // Development fallback when GOOGLE_CLIENT_ID has not been set yet
    try {
      const parts = input.idToken.split(".");
      const payloadPart = parts[1];
      if (payloadPart) {
        const decoded = JSON.parse(Buffer.from(payloadPart, "base64").toString("utf-8")) as {
          email?: string;
          name?: string;
          picture?: string;
        };
        if (decoded.email) {
          const emailVal = decoded.email.toLowerCase();
          email = emailVal;
          fullName = decoded.name ?? emailVal.split("@")[0] ?? "Google User";
          avatarUrl = decoded.picture ?? null;
        }
      }
    } catch {
      // ignore
    }
    if (!email) {
      if (input.idToken.startsWith("mock_")) {
        const mockEmail = input.idToken.replace("mock_", "").toLowerCase();
        email = mockEmail.includes("@") ? mockEmail : `${mockEmail}@gmail.com`;
        fullName = "Google Dev User";
      } else {
        throw new HttpError(400, "GOOGLE_CONFIG_MISSING", "Server chưa cấu hình GOOGLE_CLIENT_ID trong file .env");
      }
    }
  }

  if (!email) {
    throw new HttpError(400, "INVALID_TOKEN", "Không tìm thấy email hợp lệ trong mã xác thực Google");
  }

  let user = await prisma.user.findUnique({ where: { email } });
  let isNewUser = false;

  if (!user) {
    isNewUser = true;
    user = await prisma.user.create({
      data: {
        email,
        fullName,
        avatarUrl,
        passwordHash: null,
      },
    });
  } else if (!user.avatarUrl && avatarUrl) {
    user = await prisma.user.update({
      where: { id: user.id },
      data: { avatarUrl },
    });
  }

  const publicUser = await prisma.user.findUniqueOrThrow({
    where: { id: user.id },
    select: publicUserSelect,
  });

  const tokens = await issueTokens(user.id);
  return {
    user: presentUser(publicUser),
    ...tokens,
    isNewUser,
  };
}

