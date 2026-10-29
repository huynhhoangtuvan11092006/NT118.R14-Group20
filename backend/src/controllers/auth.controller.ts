import type { Request, Response } from "express";
import * as authService from "../services/auth.service.js";
import { loginSchema, refreshSchema, registerSchema } from "../validators/auth.validator.js";

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
