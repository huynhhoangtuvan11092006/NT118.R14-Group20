import type { Request, Response } from "express";
import * as readingService from "../services/reading.service.js";
import { submitReadingSchema } from "../validators/reading.validator.js";

export async function getReadingTests(_req: Request, res: Response) {
  const userId = res.locals.authUserId as string | undefined;
  const tests = await readingService.getReadingTests(userId);
  res.status(200).json(tests);
}

export async function getReadingTestDetail(req: Request, res: Response) {
  const testId = req.params.id as string;
  const testDetail = await readingService.getReadingTestDetail(testId);
  res.status(200).json(testDetail);
}

export async function submitReading(req: Request, res: Response) {
  const input = submitReadingSchema.parse(req.body);
  const userId = res.locals.authUserId as string | undefined;
  const result = await readingService.gradeReadingSubmission(userId ?? null, input);
  res.status(200).json(result);
}

export async function getSubmissionDetail(req: Request, res: Response) {
  const submissionId = req.params.id as string;
  const userId = res.locals.authUserId as string | undefined;
  const result = await readingService.getSubmissionDetail(submissionId, userId);
  res.status(200).json(result);
}
