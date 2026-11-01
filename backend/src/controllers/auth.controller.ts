import type { Request, Response } from "express";
import * as authService from "../services/auth.service.js";
import { forgotPasswordSchema, googleAuthSchema, loginSchema, refreshSchema, registerSchema, resetPasswordSchema } from "../validators/auth.validator.js";

export async function register(req: Request, res: Response) {
  const input = registerSchema.parse(req.body);
  res.status(201).json(await authService.register(input));
}

export async function login(req: Request, res: Response) {
  const input = loginSchema.parse(req.body);
  res.status(200).json(await authService.login(input));
}

export async function refresh(req: Request, res: Response) {
  const input = refreshSchema.parse(req.body);
  res.status(200).json(await authService.refresh(input.refreshToken));
}

export async function logout(req: Request, res: Response) {
  const input = refreshSchema.parse(req.body);
  await authService.logout(input.refreshToken);
  res.status(204).send();
}

export async function forgotPassword(req: Request, res: Response) {
  const input = forgotPasswordSchema.parse(req.body);
  res.status(200).json(await authService.forgotPassword(input));
}

export async function resetPassword(req: Request, res: Response) {
  const input = resetPasswordSchema.parse(req.body);
  res.status(200).json(await authService.resetPassword(input));
}

export async function googleAuth(req: Request, res: Response) {
  const input = googleAuthSchema.parse(req.body);
  res.status(200).json(await authService.googleAuth(input));
}

