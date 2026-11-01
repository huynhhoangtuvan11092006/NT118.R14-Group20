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

export const forgotPasswordSchema = z.strictObject({
  email: z.email().max(320).transform((email) => email.trim().toLowerCase()),
});

export const resetPasswordSchema = z.strictObject({
  email: z.email().max(320).transform((email) => email.trim().toLowerCase()),
  otp: z.string().trim().length(6, "Mã OTP phải gồm đúng 6 chữ số"),
  newPassword: z.string().min(8, "Mật khẩu phải có tối thiểu 8 ký tự").max(128),
});

export const googleAuthSchema = z.strictObject({
  idToken: z.string().min(1, "Google ID token is required"),
});

