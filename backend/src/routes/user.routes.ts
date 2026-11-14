import { Router } from "express";
import { getMe, getMyProgress, updateMe, updateMyProgress, updateTargetBand } from "../controllers/user.controller.js";
import { requireAuth } from "../middlewares/auth.middleware.js";

export const userRouter = Router();
userRouter.use(requireAuth);
userRouter.get("/me", getMe);
userRouter.put("/me", updateMe);
userRouter.put("/target-band", updateTargetBand);
userRouter.get("/me/progress", getMyProgress);
userRouter.put("/me/progress", updateMyProgress);
