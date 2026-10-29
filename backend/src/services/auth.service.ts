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
import type { loginSchema, registerSchema } from "../validators/auth.validator.js";

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
