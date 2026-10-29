import type { NextFunction, Request, Response } from "express";
import { verifyAccessToken } from "../lib/jwt.js";

export function requireAuth(req: Request, res: Response, next: NextFunction): void {
  const header = req.header("authorization");
  const match = /^Bearer ([^\s]+)$/i.exec(header ?? "");
  if (!match?.[1]) {
    res.status(401).json({ error: "UNAUTHORIZED", message: "Bearer access token required" });
    return;
  }
  try {
    res.locals.authUserId = verifyAccessToken(match[1]).userId;
    next();
  } catch {
    res.status(401).json({ error: "UNAUTHORIZED", message: "Invalid or expired access token" });
  }
}
