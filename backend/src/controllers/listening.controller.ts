import type { Request, Response } from "express";
import * as listeningService from "../services/listening.service.js";
import { submitListeningSchema } from "../validators/listening.validator.js";

export async function getListeningTests(_req: Request, res: Response) {
  const userId = res.locals.authUserId as string | undefined;
  const tests = await listeningService.getListeningTests(userId);
  res.status(200).json(tests);
}

export async function getListeningTestDetail(req: Request, res: Response) {
  const testId = req.params.id as string;
  const testDetail = await listeningService.getListeningTestDetail(testId);
  res.status(200).json(testDetail);
}

export async function submitListening(req: Request, res: Response) {
  const input = submitListeningSchema.parse(req.body);
  const userId = res.locals.authUserId as string | undefined;
  const result = await listeningService.gradeListeningSubmission(userId ?? null, input);
  res.status(200).json(result);
}

export async function getListeningSubmissionDetail(req: Request, res: Response) {
  const submissionId = req.params.id as string;
  const userId = res.locals.authUserId as string | undefined;
  const result = await listeningService.getListeningSubmissionDetail(submissionId, userId);
  res.status(200).json(result);
}

