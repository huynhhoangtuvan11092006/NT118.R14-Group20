import { z } from "zod";

export const submitListeningSchema = z.strictObject({
  testId: z.string().uuid("Test ID phải là UUID hợp lệ"),
  answers: z.record(z.string(), z.string()),
  timeSpentSeconds: z.number().int().nonnegative().optional(),
});

export type SubmitListeningInput = z.infer<typeof submitListeningSchema>;

