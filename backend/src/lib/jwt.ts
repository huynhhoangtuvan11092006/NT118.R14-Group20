import { randomUUID, createHash } from "node:crypto";
import jwt, { type JwtPayload } from "jsonwebtoken";
import { env } from "../config/env.js";
import { HttpError } from "./http-error.js";

const JWT_ISSUER = "bandup-backend";
const JWT_AUDIENCE = "bandup-android";
export const ACCESS_TTL_SECONDS = 15 * 60;
export const REFRESH_TTL_SECONDS = 7 * 24 * 60 * 60;

type VerifiedToken = { userId: string; jti?: string };

export function signAccessToken(userId: string): string {
  return jwt.sign({ tokenType: "access" }, env.JWT_ACCESS_SECRET, {
    algorithm: "HS256", subject: userId, issuer: JWT_ISSUER, audience: JWT_AUDIENCE,
    expiresIn: ACCESS_TTL_SECONDS,
  });
}

export function signRefreshToken(userId: string, jti: string): string {
  return jwt.sign({ tokenType: "refresh" }, env.JWT_REFRESH_SECRET, {
    algorithm: "HS256", subject: userId, jwtid: jti, issuer: JWT_ISSUER,
    audience: JWT_AUDIENCE, expiresIn: REFRESH_TTL_SECONDS,
  });
}

function decode(token: string, secret: string, kind: "access" | "refresh"): VerifiedToken {
  try {
    const payload = jwt.verify(token, secret, {
      algorithms: ["HS256"], issuer: JWT_ISSUER, audience: JWT_AUDIENCE,
    }) as JwtPayload | string;
    if (typeof payload === "string" || payload.tokenType !== kind ||
        typeof payload.sub !== "string" ||
        !/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(payload.sub)) {
      throw new Error("Invalid token claims");
    }
    if (kind === "refresh" && (typeof payload.jti !== "string" ||
        !/^[0-9a-f-]{36}$/i.test(payload.jti))) {
      throw new Error("Invalid refresh token ID");
    }
    return { userId: payload.sub, jti: payload.jti };
  } catch {
    throw new HttpError(401, "UNAUTHORIZED", "Invalid or expired token");
  }
}

export const verifyAccessToken = (token: string): VerifiedToken => decode(token, env.JWT_ACCESS_SECRET, "access");
export const verifyRefreshToken = (token: string): VerifiedToken => decode(token, env.JWT_REFRESH_SECRET, "refresh");
export const createRefreshId = (): string => randomUUID();
export const hashRefreshToken = (token: string): string => createHash("sha256").update(token).digest("hex");
