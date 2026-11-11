import { z } from "zod";

const criterionSchema = z.object({
  score: z.number().min(0).max(9).transform((score) => Math.round(score * 2) / 2),
  feedback: z.string().trim().min(5).max(3500),
  evidence: z.string().trim().max(1000).optional().default(""),
});

export const writingEvaluationInputSchema = z.object({
  taskType: z.enum(["ACADEMIC_TASK_1", "GENERAL_TASK_1", "TASK_2"]),
  question: z.string().trim().min(10).max(3500),
  essay: z.string().trim().min(30).max(14000),
  sourceMaterial: z.string().trim().min(20).max(6000).optional(),
  targetBand: z.number().min(4).max(9).optional(),
}).superRefine((value, ctx) => {
  if (value.taskType === "ACADEMIC_TASK_1" && !value.sourceMaterial) {
    ctx.addIssue({ code: "custom", path: ["sourceMaterial"], message: "Academic Task 1 requires a chart/table/process description to assess accuracy" });
  }
});
export type WritingEvaluationInput = z.infer<typeof writingEvaluationInputSchema>;

export const geminiWritingOutputSchema = z.object({
  criteria: z.object({
    taskAchievementOrResponse: criterionSchema,
    coherenceCohesion: criterionSchema,
    lexicalResource: criterionSchema,
    grammaticalRangeAccuracy: criterionSchema,
  }),
  overallFeedback: z.string().trim().min(5).max(3500),
  improvementPlan: z.array(z.string().trim().min(3).max(1000)).min(1).max(5),
  grammarCorrections: z.array(z.object({
    original: z.string().trim().min(1).max(1000),
    rewrite: z.string().trim().min(1).max(1000),
    explanation: z.string().trim().min(3).max(1000),
  })).max(10),
  modelEssay: z.string().trim().min(20).max(8000).optional(),
});
export type GeminiWritingOutput = z.infer<typeof geminiWritingOutputSchema>;
