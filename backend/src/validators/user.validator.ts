import { z } from "zod";

const band = z.number().min(0).max(9).refine((value) => Number.isInteger(value * 2), {
  message: "Band score must be in 0.5 increments between 0 and 9",
});

const validDate = z.iso.date().refine((date) => {
  const actual = new Date(`${date}T00:00:00.000Z`);
  return !Number.isNaN(actual.getTime()) && actual.toISOString().slice(0, 10) === date;
}, "Invalid calendar date");

export const updateProfileSchema = z.strictObject({
  fullName: z.string().trim().min(2).max(150).optional(),
  avatarUrl: z.url().max(2048).nullable().optional(),
  currentBand: band.nullable().optional(),
  expectedExamDate: validDate.nullable().optional(),
  dailyStudyMinutes: z.number().int().min(1).max(1440).nullable().optional(),
}).refine((input) => Object.keys(input).length > 0, "Provide at least one field");

export const targetBandSchema = z.strictObject({ targetBand: band });
