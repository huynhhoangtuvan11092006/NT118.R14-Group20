import type { Request, Response } from "express";
import { prisma } from "../config/database.js";
import { HttpError } from "../lib/http-error.js";
import { publicUserSelect, presentUser } from "../services/auth.service.js";
import { targetBandSchema, updateProfileSchema } from "../validators/user.validator.js";

function authenticatedId(res: Response): string {
  const userId: unknown = res.locals.authUserId;
  if (typeof userId !== "string") throw new HttpError(401, "UNAUTHORIZED", "Authentication required");
  return userId;
}

export async function getMe(req: Request, res: Response) {
  const user = await prisma.user.findUnique({
    where: { id: authenticatedId(res) }, select: publicUserSelect,
  });
  if (!user) throw new HttpError(401, "UNAUTHORIZED", "Account not found");
  res.json({ user: presentUser(user) });
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
