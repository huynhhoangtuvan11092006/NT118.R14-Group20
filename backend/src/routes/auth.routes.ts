import { Router } from "express";
import { forgotPassword, googleAuth, login, logout, refresh, register, resetPassword } from "../controllers/auth.controller.js";

export const authRouter = Router();
authRouter.post("/register", register);
authRouter.post("/login", login);
authRouter.post("/refresh", refresh);
authRouter.post("/logout", logout);
authRouter.post("/forgot-password", forgotPassword);
authRouter.post("/reset-password", resetPassword);
authRouter.post("/google", googleAuth);

