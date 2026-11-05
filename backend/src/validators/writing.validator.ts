import { z } from "zod";

const criterionSchema = z.strictObject({
  score: z.number().min(0).max(9).refine((score) => Number.isInteger(score * 2), "Band must be a multiple of 0.5"),
  feedback: z.string().trim().min(10).max(1800),
  evidence: z.string().trim().min(1).max(450),
});

export const writingEvaluationInputSchema = z.strictObject({
  taskType: z.enum(["ACADEMIC_TASK_1", "GENERAL_TASK_1", "TASK_2"]),
  question: z.string().trim().min(10).max(3500),
  essay: z.string().trim().min(30).max(14000),
  sourceMaterial: z.string().trim().min(20).max(6000).optional(),
}).superRefine((value, ctx) => {
  if (value.taskType === "ACADEMIC_TASK_1" && !value.sourceMaterial) {
    ctx.addIssue({ code: "custom", path: ["sourceMaterial"], message: "Academic Task 1 requires a chart/table/process description to assess accuracy" });
  }
});
export type WritingEvaluationInput = z.infer<typeof writingEvaluationInputSchema>;

export const geminiWritingOutputSchema = z.strictObject({
  criteria: z.strictObject({
    taskAchievementOrResponse: criterionSchema,
    coherenceCohesion: criterionSchema,
    lexicalResource: criterionSchema,
    grammaticalRangeAccuracy: criterionSchema,
  }),
  overallFeedback: z.string().trim().min(15).max(2200),
  improvementPlan: z.array(z.string().trim().min(6).max(450)).min(1).max(3),
  grammarCorrections: z.array(z.strictObject({
    original: z.string().trim().min(3).max(700),
    rewrite: z.string().trim().min(3).max(700),
    explanation: z.string().trim().min(6).max(500),
  })).max(5),
});
export type GeminiWritingOutput = z.infer<typeof geminiWritingOutputSchema>;
