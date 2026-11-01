import { z } from "zod";

export const submitReadingSchema = z.strictObject({
  testId: z.string().uuid("Test ID phải là UUID hợp lệ"),
  answers: z.record(z.string(), z.string()),
});

export type SubmitReadingInput = z.infer<typeof submitReadingSchema>;
