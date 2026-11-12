import { Router } from "express";
import { evaluateWritingController, generateRoadmapController } from "../controllers/ai_evaluation.controller.js";
import { optionalAuth } from "../middlewares/auth.middleware.js";

export const aiEvaluationRouter = Router();
aiEvaluationRouter.use(optionalAuth);
aiEvaluationRouter.post("/writing", evaluateWritingController);
aiEvaluationRouter.post("/roadmap", generateRoadmapController);

