import { z } from "zod";

export const registerSchema = z.strictObject({
  email: z.email().max(320).transform((email) => email.trim().toLowerCase()),
  password: z.string().min(8).max(128),
  fullName: z.string().trim().min(2).max(150),
});

export const loginSchema = z.strictObject({
  email: z.email().max(320).transform((email) => email.trim().toLowerCase()),
  password: z.string().min(1),
});

export const refreshSchema = z.strictObject({ refreshToken: z.string().min(1) });
