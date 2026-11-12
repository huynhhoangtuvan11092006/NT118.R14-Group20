import { z } from "zod";
import { env } from "../config/env.js";
import { HttpError } from "../lib/http-error.js";

const GEMINI_API_BASE = "https://generativelanguage.googleapis.com/v1beta/models";

export const roadmapInputSchema = z.object({
  currentBand: z.number().min(0).max(9),
  targetBand: z.number().min(4).max(9),
  studyGoal: z.string().default("Du học & Học bổng"),
  dailyMinutes: z.number().default(30),
  countdownLabel: z.string().default("Còn 180 ngày"),
  focusSkills: z.array(z.string()).default(["Writing", "Speaking"]),
  completedLessons: z.number().default(0),
  totalLessons: z.number().default(15),
});

export type RoadmapInput = z.infer<typeof roadmapInputSchema>;

const roadmapOutputSchema = z.object({
  strategicAdvice: z.string().min(15),
  phase1Title: z.string().min(2),
  phase1Desc: z.string().min(10),
  phase2Title: z.string().min(2),
  phase2Desc: z.string().min(10),
  phase3Title: z.string().min(2),
  phase3Desc: z.string().min(10),
});

const roadmapJsonSchema = {
  type: "object",
  properties: {
    strategicAdvice: {
      type: "string",
      description: "2-3 câu lời khuyên chiến lược bằng tiếng Việt dành riêng cho học viên dựa trên Band hiện tại, Band mục tiêu, kỹ năng ưu tiên và số bài đã làm.",
    },
    phase1Title: { type: "string", description: "Tên ngắn gọn Chặng 1 (ví dụ: Nền tảng)" },
    phase1Desc: {
      type: "string",
      description: "Mô tả đúng 5 bài học thực tế của Chặng 1 trên app (Reading Cam 19 Test 1, Listening Cam 19 Test 1, Reading Cam 18 Test 1, Writing Task 1 Urban Population, Từ vựng Environment).",
    },
    phase2Title: { type: "string", description: "Tên ngắn gọn Chặng 2 (ví dụ: Bứt phá kỹ năng)" },
    phase2Desc: {
      type: "string",
      description: "Mô tả đúng 5 bài học thực tế của Chặng 2 trên app (Reading Cam 19 Test 2, Listening Cam 19 Test 2, Reading Cam 18 Test 2, Writing Task 2 Aim of Science, Từ vựng Education).",
    },
    phase3Title: { type: "string", description: "Tên ngắn gọn Chặng 3 (ví dụ: Đỉnh cao)" },
    phase3Desc: {
      type: "string",
      description: "Mô tả đúng 5 bài học thực tế của Chặng 3 trên app (Reading Cam 19 Test 3, Listening Cam 19 Test 3, Writing Task 1 US Income, Writing Task 2 University, Từ vựng Technology).",
    },
  },
  required: [
    "strategicAdvice",
    "phase1Title",
    "phase1Desc",
    "phase2Title",
    "phase2Desc",
    "phase3Title",
    "phase3Desc",
  ],
} as const;

function cleanJsonString(raw: string): string {
  let cleaned = raw.trim();
  cleaned = cleaned.replace(/<thought>[\s\S]*?<\/thought>/gi, "").trim();
  if (cleaned.startsWith("```")) {
    cleaned = cleaned.replace(/^```(?:json)?\s*/i, "").replace(/\s*```$/, "").trim();
  }
  const firstBrace = cleaned.indexOf("{");
  const lastBrace = cleaned.lastIndexOf("}");
  if (firstBrace !== -1 && lastBrace !== -1 && lastBrace > firstBrace) {
    cleaned = cleaned.substring(firstBrace, lastBrace + 1);
  }
  return cleaned;
}

function extractModelText(value: unknown): string | undefined {
  if (!value || typeof value !== "object") return undefined;
  const record = value as Record<string, unknown>;
  if ("choices" in record && Array.isArray(record.choices) && record.choices.length > 0) {
    const firstChoice = record.choices[0] as Record<string, unknown> | undefined;
    const msg = firstChoice?.message as Record<string, unknown> | undefined;
    if (typeof msg?.content === "string") return msg.content;
  }
  if ("candidates" in record && Array.isArray(record.candidates) && record.candidates.length > 0) {
    const candidate = record.candidates[0] as Record<string, unknown> | undefined;
    const content = candidate?.content as Record<string, unknown> | undefined;
    if (content && Array.isArray(content.parts)) {
      const texts = content.parts
        .filter((p): p is { text: string } => !!p && typeof p === "object" && "text" in p && typeof p.text === "string")
        .map((p) => p.text);
      if (texts.length > 0) return texts.join("");
    }
  }
  return undefined;
}

export async function generateAiRoadmap(input: RoadmapInput) {
  const apiKey = env.AI_API_KEY ?? env.GEMINI_API_KEY;
  if (!apiKey || apiKey.startsWith("CHANGE_ME")) {
    throw new HttpError(503, "AI_NOT_CONFIGURED", "Gemini API key is not configured");
  }

  const candidateModels = [
    "gemini-3.5-flash-lite",
    "gemini-3.5-flash-lite",
    "gemini-3.5-flash",
  ];
  const models = candidateModels;

  const bandGap = Math.max(0.5, Number((input.targetBand - input.currentBand).toFixed(1)));
  const midBand1 = Number((input.currentBand + bandGap * 0.35).toFixed(1));
  const midBand2 = Number((input.currentBand + bandGap * 0.7).toFixed(1));

  const prompt = `
Bạn là Cố vấn Chiến lược IELTS AI trên ứng dụng BandUp IELTS.
Hãy phân tích hồ sơ học viên sau và trả về DUY NHẤT 1 JSON object hợp lệ bằng TIẾNG VIỆT:
- Band hiện tại: ${input.currentBand.toFixed(1)} -> Band mục tiêu: ${input.targetBand.toFixed(1)} (Tăng +${bandGap.toFixed(1)} Band)
- Mục đích: ${input.studyGoal} | Cam kết: ${input.dailyMinutes} phút/ngày | Thời hạn: ${input.countdownLabel}
- Kỹ năng ưu tiên: ${input.focusSkills.join(", ")}
- Tiến độ thực tế: Đã hoàn thành ${input.completedLessons}/${input.totalLessons} bài trong lộ trình.
- Các bài học thực tế có sẵn trên app (chia làm 3 chặng, mỗi chặng đúng 5 bài):
  + Chặng 1 (5 bài): Reading Cam 19 Test 1, Listening Cam 19 Test 1, Reading Cam 18 Test 1, Writing Task 1 (Urban Population), Từ vựng Environment.
  + Chặng 2 (5 bài): Reading Cam 19 Test 2, Listening Cam 19 Test 2, Reading Cam 18 Test 2, Writing Task 2 (Aim of Science), Từ vựng Education.
  + Chặng 3 (5 bài): Reading Cam 19 Test 3, Listening Cam 19 Test 3, Writing Task 1 (US Income), Writing Task 2 (University), Từ vựng Technology.
Hãy trả về JSON đúng 7 trường string sau:
{
  "strategicAdvice": "2-3 câu lời khuyên chiến lược sắc bén dành riêng cho học viên",
  "phase1Title": "Chặng 1: Nền tảng (${input.currentBand.toFixed(1)} - ${midBand1.toFixed(1)})",
  "phase1Desc": "Mô tả 5 bài học Chặng 1",
  "phase2Title": "Chặng 2: Bứt phá kỹ năng (${midBand1.toFixed(1)} - ${midBand2.toFixed(1)})",
  "phase2Desc": "Mô tả 5 bài học Chặng 2",
  "phase3Title": "Chặng 3: Đỉnh cao (${midBand2.toFixed(1)} - ${input.targetBand.toFixed(1)}+)",
  "phase3Desc": "Mô tả 5 bài học Chặng 3"
}
`.trim();

  const isLocalhostProxy = !!env.AI_BASE_URL && (env.AI_BASE_URL.includes("localhost") || env.AI_BASE_URL.includes("127.0.0.1"));
  const isCustomRouter = !!env.AI_BASE_URL && (!isLocalhostProxy || !env.GEMINI_API_KEY);
  const effectiveApiKey = isCustomRouter
    ? (apiKey || env.AI_API_KEY || env.GEMINI_API_KEY)
    : (env.GEMINI_API_KEY || apiKey);

  for (let i = 0; i < models.length; i++) {
    const model = models[i]!;
    try {
      const requestUrl = isCustomRouter
        ? (env.AI_BASE_URL!.includes("/chat/completions") ? env.AI_BASE_URL! : `${env.AI_BASE_URL!.replace(/\/+$/, "")}/chat/completions`)
        : `${GEMINI_API_BASE}/${model}:generateContent?key=${encodeURIComponent(effectiveApiKey!)}`;

      const requestHeaders: Record<string, string> = { "content-type": "application/json" };
      if (isCustomRouter) {
        requestHeaders["Authorization"] = `Bearer ${effectiveApiKey}`;
      } else {
        requestHeaders["x-goog-api-key"] = effectiveApiKey!;
      }

      const payload = isCustomRouter
        ? JSON.stringify({
            model,
            messages: [
              {
                role: "system",
                content: "You are an official IELTS Study Planner AI. Return ONLY a valid JSON object with fields: strategicAdvice, phase1Title, phase1Desc, phase2Title, phase2Desc, phase3Title, phase3Desc.",
              },
              { role: "user", content: prompt },
            ],
            response_format: { type: "json_object" },
            temperature: 0.2,
          })
        : JSON.stringify({
            contents: [{ role: "user", parts: [{ text: prompt }] }],
            generationConfig: {
              temperature: 0.2,
              maxOutputTokens: 900,
              responseMimeType: "application/json",
            },
          });

      const res = await fetch(requestUrl, {
        method: "POST",
        headers: requestHeaders,
        body: payload,
        signal: AbortSignal.timeout(7_000),
      });

      if (!res.ok) {
        if (i < models.length - 1) {
          await new Promise((r) => setTimeout(r, 600));
        }
        continue;
      }
      const envelope = await res.json();
      const rawText = extractModelText(envelope);
      if (!rawText) continue;

      const parsed = roadmapOutputSchema.safeParse(JSON.parse(cleanJsonString(rawText)));
      if (parsed.success) {
        return {
          ...parsed.data,
          model,
        };
      }
    } catch {
      // try next attempt
    }
  }

  const focusLabel = input.focusSkills.join(" & ");
  return {
    strategicAdvice: `Với mục tiêu tăng +${bandGap.toFixed(1)} Band (${input.currentBand.toFixed(1)} → ${input.targetBand.toFixed(1)}) cho mục đích ${input.studyGoal} (${input.countdownLabel}), bạn nên duy trì ${input.dailyMinutes} phút/ngày ưu tiên ${focusLabel}. Hiện tại bạn đã hoàn thành ${input.completedLessons}/${input.totalLessons} bài; hãy tập trung xử lý dứt điểm 5 bài nền tảng ở Chặng 1 trước khi nâng chuẩn học thuật ở Chặng 2 và Chặng 3.`,
    phase1Title: `Chặng 1: Nền tảng (${input.currentBand.toFixed(1)} - ${midBand1.toFixed(1)})`,
    phase1Desc: `Hoàn thành 5 bài: Reading Cam 19 Test 1, Listening Cam 19 Test 1, Reading Cam 18 Test 1, Writing Task 1 (Urban Population) & Từ vựng Environment.`,
    phase2Title: `Chặng 2: Bứt phá kỹ năng (${midBand1.toFixed(1)} - ${midBand2.toFixed(1)})`,
    phase2Desc: `Hoàn thành 5 bài: Reading Cam 19 Test 2, Listening Cam 19 Test 2, Reading Cam 18 Test 2, Writing Task 2 (Aim of Science) & Từ vựng Education.`,
    phase3Title: `Chặng 3: Đỉnh cao (${midBand2.toFixed(1)} - ${input.targetBand.toFixed(1)}+)`,
    phase3Desc: `Hoàn thành 5 bài: Reading Cam 19 Test 3, Listening Cam 19 Test 3, Writing Task 1 (US Income), Writing Task 2 (University) & Từ vựng Technology.`,
    model: "gemini-3.5-flash-lite",
  };
}
