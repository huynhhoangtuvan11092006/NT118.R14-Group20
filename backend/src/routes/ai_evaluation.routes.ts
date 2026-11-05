import { Router } from "express";
import { evaluateWritingController } from "../controllers/ai_evaluation.controller.js";
import { requireAuth } from "../middlewares/auth.middleware.js";

export const aiEvaluationRouter = Router();
aiEvaluationRouter.use(requireAuth);
aiEvaluationRouter.post("/writing", evaluateWritingController);
