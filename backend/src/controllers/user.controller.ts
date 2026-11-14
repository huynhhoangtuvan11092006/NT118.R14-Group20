import type { Request, Response } from "express";
import { prisma } from "../config/database.js";
import { HttpError } from "../lib/http-error.js";
import { publicUserSelect, presentUser } from "../services/auth.service.js";
import { syncProgressSchema, targetBandSchema, updateProfileSchema } from "../validators/user.validator.js";

const CLOUD_SYNC_WORD_KEY = "__CLOUD_SYNC_STATE_V1__";

function authenticatedId(res: Response): string {
  const userId: unknown = res.locals.authUserId;
  if (typeof userId !== "string") throw new HttpError(401, "UNAUTHORIZED", "Authentication required");
  return userId;
}

export async function getMe(req: Request, res: Response) {
  const userId = authenticatedId(res);
  const user = await prisma.user.findUnique({
    where: { id: userId }, select: publicUserSelect,
  });
  if (!user) throw new HttpError(401, "UNAUTHORIZED", "Account not found");
  res.json({ user: presentUser(user) });
}

export async function getMyProgress(_req: Request, res: Response) {
  const userId = authenticatedId(res);
  const row = await prisma.vocabulary.findUnique({
    where: { userId_word: { userId, word: CLOUD_SYNC_WORD_KEY } },
  });
  res.json({
    progressJson: row?.meaning ?? null,
    currentStreak: row?.reviewCount ?? 0,
    updatedAt: row?.updatedAt?.toISOString() ?? null,
  });
}

export async function updateMyProgress(req: Request, res: Response) {
  const userId = authenticatedId(res);
  const input = syncProgressSchema.parse(req.body);
  const row = await prisma.vocabulary.upsert({
    where: { userId_word: { userId, word: CLOUD_SYNC_WORD_KEY } },
    create: {
      userId,
      word: CLOUD_SYNC_WORD_KEY,
      meaning: input.progressJson,
      isLearned: true,
      reviewCount: input.currentStreak ?? 0,
      lastReviewedAt: new Date(),
    },
    update: {
      meaning: input.progressJson,
      isLearned: true,
      reviewCount: input.currentStreak ?? 0,
      lastReviewedAt: new Date(),
    },
  });
  res.json({
    progressJson: row.meaning,
    currentStreak: row.reviewCount,
    updatedAt: row.updatedAt.toISOString(),
  });
}

export async function updateMe(req: Request, res: Response) {
  const input = updateProfileSchema.parse(req.body);
  const { expectedExamDate, ...fields } = input;
  const updated = await prisma.user.updateMany({
    where: { id: authenticatedId(res) },
    data: {
      ...fields,
      ...(expectedExamDate === undefined ? {} : {
        expectedExamDate: expectedExamDate === null ? null : new Date(`${expectedExamDate}T00:00:00.000Z`),
      }),
    },
  });
  if (updated.count !== 1) throw new HttpError(401, "UNAUTHORIZED", "Account not found");
  return getMe(req, res);
}

export async function updateTargetBand(req: Request, res: Response) {
  const { targetBand } = targetBandSchema.parse(req.body);
  const updated = await prisma.user.updateMany({
    where: { id: authenticatedId(res) }, data: { targetBand },
  });
  if (updated.count !== 1) throw new HttpError(401, "UNAUTHORIZED", "Account not found");
  return getMe(req, res);
}
