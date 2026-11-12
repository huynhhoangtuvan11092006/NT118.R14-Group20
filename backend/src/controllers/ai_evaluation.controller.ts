import type { Request, Response } from "express";
import { generateAiRoadmap, roadmapInputSchema } from "../services/ai_roadmap.service.js";
import { evaluateWriting } from "../services/ai_writing.service.js";
import { writingEvaluationInputSchema } from "../validators/writing.validator.js";

/** Private endpoint. Auth middleware provides res.locals.authUserId. */
export async function evaluateWritingController(req: Request, res: Response): Promise<void> {
  const input = writingEvaluationInputSchema.parse(req.body);
  const evaluation = await evaluateWriting(input);
  res.status(200).json(evaluation);
}

export async function generateRoadmapController(req: Request, res: Response): Promise<void> {
  const input = roadmapInputSchema.parse(req.body);
  const roadmap = await generateAiRoadmap(input);
  res.status(200).json(roadmap);
}

