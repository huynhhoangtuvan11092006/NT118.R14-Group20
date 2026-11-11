
import { env } from "../config/env.js";
import { HttpError } from "../lib/http-error.js";
import {
  buildWritingPrompt,
  IELTS_WRITING_SYSTEM_PROMPT,
  writingEvaluationJsonSchema,
} from "../prompts/ielts_writing_prompt.js";
import {
  geminiWritingOutputSchema,
  type WritingEvaluationInput,
} from "../validators/writing.validator.js";

const GEMINI_API_BASE =
  "https://generativelanguage.googleapis.com/v1beta/models";

const CRITERIA = [
  "taskAchievementOrResponse",
  "coherenceCohesion",
  "lexicalResource",
  "grammaticalRangeAccuracy",
] as const;

// Timeout tối đa cho mỗi lần gọi Gemini.
// Tổng timeout lấy từ GEMINI_TIMEOUT_MS.
const PER_ATTEMPT_TIMEOUT_MS = 35_000;
const MIN_REQUEST_TIME_MS = 350;
const DEFAULT_FALLBACK_MODEL = "gemini-3.6-flash";

interface EvaluateWritingOptions {
  fetchImpl?: typeof fetch;
  apiKey?: string;
  model?: string;
  timeoutMs?: number;
  fallbackModel?: string;
}

function isValidModel(model: string): boolean {
  if (env.AI_BASE_URL) return model.trim().length > 0;
  return /^[a-z0-9._/-]+$/i.test(model);
}

// Lấy phần JSON text từ response (hỗ trợ cả Google Gemini và 9Router/OpenAI).
function extractModelText(
  value: unknown,
): string | undefined {
  if (!value || typeof value !== "object") {
    return undefined;
  }

  // 1. Định dạng OpenAI / 9Router: { choices: [{ message: { content: "..." } }] }
  const record = value as Record<string, unknown>;
  if ("choices" in record && Array.isArray(record.choices) && record.choices.length > 0) {
    const firstChoice = record.choices[0] as Record<string, unknown> | undefined;
    if (firstChoice && typeof firstChoice === "object" && "message" in firstChoice) {
      const msg = firstChoice.message as Record<string, unknown> | undefined;
      if (msg && typeof msg === "object" && typeof msg.content === "string") {
        return msg.content;
      }
    }
  }

  // 2. Định dạng Google Gemini: { candidates: [{ content: { parts: [{ text: "..." }] } }] }
  if ("candidates" in record && Array.isArray(record.candidates) && record.candidates.length > 0) {
    const candidate = record.candidates[0] as Record<string, unknown> | undefined;
    if (candidate && typeof candidate === "object" && "content" in candidate) {
      const content = candidate.content as Record<string, unknown> | undefined;
      if (content && typeof content === "object" && "parts" in content && Array.isArray(content.parts)) {
        const texts = content.parts
          .filter(
            (part): part is { text: string } =>
              !!part && typeof part === "object" && "text" in part && typeof part.text === "string",
          )
          .map((part) => part.text);
        if (texts.length > 0) {
          return texts.join("");
        }
      }
    }
  }

  return undefined;
}

// Chờ một khoảng ngắn trước khi retry.
async function pauseBeforeRetry(
  remainingMs: number,
  retryIndex: number,
): Promise<void> {
  const delay = Math.min(
    remainingMs - MIN_REQUEST_TIME_MS,
    Math.min(1_600, 650 * 2 ** retryIndex) +
      Math.floor(Math.random() * 250),
  );

  if (delay > 0) {
    await new Promise<void>((resolve) =>
      setTimeout(resolve, delay),
    );
  }
}

/**
 * Xác minh evidence có thực sự xuất hiện
 * trong bài Writing của người dùng không.
 *
 * Chấp nhận:
 * - Trích dẫn khớp nguyên văn.
 * - Gemini thêm dấu ngoặc kép ở bên ngoài.
 * - Khác biệt khoảng trắng hoặc xuống dòng.
 *
 * Không chấp nhận:
 * - Gemini sửa từ trong evidence.
 * - Gemini tự tạo nội dung không có trong essay.
 *
 * Hàm luôn trả về đoạn trích nguyên văn từ essay.
 */
function verifyEssayEvidence(
  essay: string,
  evidence: string,
): string | undefined {
  const raw = evidence.trim();

  if (!raw) {
    return undefined;
  }

  // 1. Khớp nguyên văn.
  if (essay.includes(raw)) {
    return raw;
  }

  // 2. Loại dấu ngoặc Gemini thêm bên ngoài.
  const cleaned = raw
    .replace(/^["“”'‘’`]+|["“”'‘’`]+$/gu, "")
    .trim();

  if (!cleaned) {
    return undefined;
  }

  if (essay.includes(cleaned)) {
    return cleaned;
  }

  // 3. Cho phép khác biệt khoảng trắng.
  const escapedWords = cleaned
    .split(/\s+/u)
    .map((word) =>
      word.replace(/[.*+?^${}()|[\]\\]/g, "\\$&"),
    );

  const pattern = new RegExp(
    escapedWords.join("\\s+"),
    "u",
  );

  return essay.match(pattern)?.[0];
}

/**
 * Main IELTS Writing AI evaluation service.
 *
 * Flow:
 * 1. Validate Gemini configuration.
 * 2. Build Writing prompt and JSON Schema.
 * 3. Call primary Gemini model.
 * 4. Retry transient errors.
 * 5. Fall back to another model when necessary.
 * 6. Validate JSON response.
 * 7. Verify evidence and grammar corrections.
 * 8. Calculate estimated IELTS band.
 * 9. Return structured evaluation.
 */
export async function evaluateWriting(
  input: WritingEvaluationInput,
  options: EvaluateWritingOptions = {},
) {
  // ===================================
  // 1. API configuration
  // ===================================

  const apiKey =
    options.apiKey ?? env.AI_API_KEY ?? env.GEMINI_API_KEY;

  if (
    !apiKey ||
    apiKey.startsWith("CHANGE_ME")
  ) {
    throw new HttpError(
      503,
      "AI_NOT_CONFIGURED",
      "AI / Gemini API key is not configured on the backend",
    );
  }

  const primaryModel =
    options.model ?? env.AI_MODEL ?? env.GEMINI_MODEL;

  const fallbackModel =
    options.fallbackModel ??
    process.env.GEMINI_FALLBACK_MODEL ??
    DEFAULT_FALLBACK_MODEL;

  if (
    !isValidModel(primaryModel) ||
    (fallbackModel && !isValidModel(fallbackModel))
  ) {
    throw new HttpError(
      503,
      "AI_NOT_CONFIGURED",
      "Invalid Gemini model configuration",
    );
  }

  const fetchImpl =
    options.fetchImpl ?? fetch;

  const totalTimeoutMs =
    options.timeoutMs ?? env.GEMINI_TIMEOUT_MS;

  if (
    !Number.isFinite(totalTimeoutMs) ||
    totalTimeoutMs <= 0
  ) {
    throw new HttpError(
      503,
      "AI_NOT_CONFIGURED",
      "Invalid Gemini timeout configuration",
    );
  }

  const start = performance.now();

  // ===================================
  // 2. Prepare essay and prompt
  // ===================================

  const wordCount = input.essay
    .trim()
    .split(/\s+/u)
    .filter(Boolean)
    .length;

  const prompt = [
    buildWritingPrompt(input),
    `Verified essay word count (calculated by the backend): ${wordCount}. ` +
      "If mentioning length, use exactly this count; do not estimate it yourself.",
  ].join("\n\n");

  const requestBody = JSON.stringify({
    systemInstruction: {
      parts: [
        {
          text:
            IELTS_WRITING_SYSTEM_PROMPT +
            "\nUse Vietnamese (not Chinese) for feedback, improvement plans and explanations.",
        },
      ],
    },

    contents: [
      {
        role: "user",
        parts: [
          {
            text: prompt,
          },
        ],
      },
    ],

    generationConfig: {
      temperature: 0.1,
      maxOutputTokens: 2800,

      thinkingConfig: {
        thinkingLevel: "low",
      },

      responseFormat: {
        text: {
          mimeType: "APPLICATION_JSON",
          schema: writingEvaluationJsonSchema,
        },
      },
    },
  });

  // ===================================
  // 3. Retry and fallback configuration
  // ===================================

  // Hai lần thử primary, một lần thử fallback.
  const attempts = [
    primaryModel,
    primaryModel,
  ];

  if (
    fallbackModel &&
    fallbackModel !== primaryModel
  ) {
    attempts.push(fallbackModel);
  }

  let lastStatus: number | undefined;
  let lastWasTimeout = false;
  let lastError: string | undefined;

  // ===================================
  // 4. Call Gemini API
  // ===================================

  for (
    let attemptIndex = 0;
    attemptIndex < attempts.length;
    attemptIndex++
  ) {
    const model = attempts[attemptIndex];

    if (!model) break;

    const remainingMs =
      totalTimeoutMs -
      (performance.now() - start);

    if (remainingMs < MIN_REQUEST_TIME_MS) {
      break;
    }

    const attemptTimeoutMs = Math.max(
      1,
      Math.floor(
        Math.min(
          remainingMs,
          PER_ATTEMPT_TIMEOUT_MS,
        ),
      ),
    );

    let upstream: Response;

    const isCustomRouter = !!env.AI_BASE_URL;
    const requestUrl = isCustomRouter
      ? (env.AI_BASE_URL!.includes("/chat/completions") ? env.AI_BASE_URL! : `${env.AI_BASE_URL!.replace(/\/+$/, "")}/chat/completions`)
      : `${GEMINI_API_BASE}/${model}:generateContent`;

    const requestHeaders: Record<string, string> = {
      "content-type": "application/json",
    };
    if (isCustomRouter) {
      requestHeaders["Authorization"] = `Bearer ${apiKey}`;
    } else {
      requestHeaders["x-goog-api-key"] = apiKey;
    }

    const payload = isCustomRouter
      ? JSON.stringify({
          model,
          messages: [
            {
              role: "system",
              content: "You are an official Cambridge IELTS Writing examiner. You must return only a valid JSON object matching the requested schema.",
            },
            {
              role: "user",
              content: prompt,
            },
          ],
          response_format: { type: "json_object" },
          temperature: 0.1,
        })
      : requestBody;

    try {
      upstream = await fetchImpl(
        requestUrl,
        {
          method: "POST",
          headers: requestHeaders,
          body: payload,
          signal: AbortSignal.timeout(
            attemptTimeoutMs,
          ),
        },
      );
    } catch (error: unknown) {
      const isTimeout =
        error instanceof Error &&
        (
          error.name === "TimeoutError" ||
          error.name === "AbortError"
        );

      lastWasTimeout = isTimeout;
      const errorMsg = error instanceof Error ? `${error.name}: ${error.message}` : String(error);
      lastError = errorMsg;

      console.warn(
        `[writing-ai] ${
          isTimeout ? "timeout" : "network error"
        } model=${model} ` +
          `attempt=${attemptIndex + 1}/${
            attempts.length
          } elapsedMs=${Math.round(
            performance.now() - start,
          )}`,
      );

      // Retry lỗi mạng hoặc timeout tạm thời.
      if (
        attemptIndex <
        attempts.length - 1
      ) {
        await pauseBeforeRetry(
          totalTimeoutMs -
            (performance.now() - start),
          attemptIndex,
        );

        continue;
      }

      if (isTimeout) {
        throw new HttpError(
          504,
          "AI_TIMEOUT",
          `AI upstream timed out at ${requestUrl}: ${errorMsg}`,
        );
      }

      throw new HttpError(
        502,
        "AI_UNAVAILABLE",
        `Could not reach AI upstream at ${requestUrl} (customRouter=${isCustomRouter}, model=${model}): ${errorMsg}`,
      );
    }

    // ===================================
    // 5. Handle Gemini HTTP errors
    // ===================================

    if (!upstream.ok) {
      lastStatus = upstream.status;
      lastWasTimeout = false;

      console.warn(
        `[writing-ai] upstream HTTP ${
          upstream.status
        } model=${model} ` +
          `attempt=${attemptIndex + 1}/${
            attempts.length
          } elapsedMs=${Math.round(
            performance.now() - start,
          )}`,
      );

      // Rate limit: không retry liên tục.
      if (upstream.status === 429) {
        throw new HttpError(
          429,
          "AI_RATE_LIMITED",
          "Gemini API quota/rate limit exceeded",
        );
      }

      // API key không hợp lệ.
      if (
        upstream.status === 401 ||
        upstream.status === 403
      ) {
        throw new HttpError(
          503,
          "AI_NOT_CONFIGURED",
          "Gemini API key is invalid or lacks access",
        );
      }

      // Retry các lỗi tạm thời.
      const transient =
        upstream.status === 408 ||
        upstream.status >= 500;

      if (
        transient &&
        attemptIndex < attempts.length - 1
      ) {
        await pauseBeforeRetry(
          totalTimeoutMs -
            (performance.now() - start),
          attemptIndex,
        );

        continue;
      }

      throw new HttpError(
        502,
        "AI_UPSTREAM_ERROR",
        `Gemini API returned HTTP ${upstream.status}`,
      );
    }

    // ===================================
    // 6. Parse Gemini response
    // ===================================

    let envelope: unknown;

    try {
      envelope = await upstream.json();
    } catch {
      throw new HttpError(
        502,
        "AI_INVALID_RESPONSE",
        "Gemini returned an invalid response envelope",
      );
    }

    const responseText =
      extractModelText(envelope);

    if (!responseText) {
      throw new HttpError(
        502,
        "AI_INVALID_RESPONSE",
        "Gemini returned no usable evaluation",
      );
    }

    let output: unknown;

    try {
      output = JSON.parse(responseText);
    } catch {
      throw new HttpError(
        502,
        "AI_INVALID_RESPONSE",
        "Gemini response was not valid JSON",
      );
    }

    // ===================================
    // 7. Validate JSON Schema
    // ===================================

    const parsed =
      geminiWritingOutputSchema.safeParse(
        output,
      );

    if (!parsed.success) {
      throw new HttpError(
        502,
        "AI_INVALID_RESPONSE",
        "Gemini response failed schema validation",
      );
    }

    const result = parsed.data;

    // ===================================
    // 8. Verify IELTS evidence
    // ===================================

    const evidenceWarnings: string[] = [];

    for (const criterion of CRITERIA) {
      const evidence =
        result.criteria[criterion].evidence;

      const verified = verifyEssayEvidence(
        input.essay,
        evidence,
      );

      if (verified === undefined) {
        // Không hiển thị dẫn chứng do AI bịa.
        // Không hủy toàn bộ kết quả chấm.
        result.criteria[criterion].evidence = "";

        evidenceWarnings.push(
          `Không xác minh được dẫn chứng ở tiêu chí ${criterion}.`,
        );

        console.warn(
          `[writing-ai] unverified evidence criterion=${criterion} model=${model}`,
        );
      } else {
        // Chỉ trả trích dẫn có thật trong essay.
        result.criteria[criterion].evidence =
          verified;
      }
    }

    // ===================================
    // 9. Verify grammar corrections
    // ===================================

    const seen = new Set<string>();

    const grammarCorrections =
      result.grammarCorrections.filter(
        (item) => {
          // Câu gốc phải tồn tại trong essay.
          if (!input.essay.includes(item.original)) {
            return false;
          }

          // Không trả về câu không thay đổi.
          if (item.original === item.rewrite) {
            return false;
          }

          // Không trả về correction trùng lặp.
          if (seen.has(item.original)) {
            return false;
          }

          seen.add(item.original);

          return true;
        },
      );

    // ===================================
    // 10. Calculate IELTS band
    // ===================================

    const mean =
      CRITERIA.reduce(
        (sum, criterion) =>
          sum +
          result.criteria[criterion].score,
        0,
      ) / 4;

    const estimatedBand =
      Math.round(mean * 2) / 2;

    // ===================================
    // 11. Build warnings
    // ===================================

    const minimumWords =
      input.taskType === "TASK_2"
        ? 250
        : 150;

    const warnings: string[] = [
      ...evidenceWarnings,
    ];

    if (wordCount < minimumWords) {
      warnings.push(
        `Bài viết có ${wordCount} từ, dưới mức ${minimumWords} từ của IELTS Writing ` +
          (
            input.taskType === "TASK_2"
              ? "Task 2."
              : "Task 1."
          ),
      );
    }

    const latencyMs = Math.round(
      performance.now() - start,
    );

    console.info(
      `[writing-ai] success model=${model} ` +
        `attempts=${attemptIndex + 1} ` +
        `latencyMs=${latencyMs}`,
    );

    // ===================================
    // 12. Return final evaluation
    // ===================================

    return {
      taskType: input.taskType,

      criterion1Name:
        input.taskType === "TASK_2"
          ? "Task Response"
          : "Task Achievement",

      criteria: result.criteria,

      estimatedBand,

      overallFeedback:
        result.overallFeedback,

      improvementPlan:
        result.improvementPlan,

      grammarCorrections,

      wordCount,

      warnings,

      model,

      latencyMs,

      disclaimer:
        "Điểm IELTS Writing ước tính bởi AI cho mục đích luyện tập, không phải điểm chấm chính thức.",
    };
  }

  // ===================================
  // 13. Handle exhausted attempts
  // ===================================

  if (lastStatus !== undefined) {
    throw new HttpError(
      502,
      "AI_UPSTREAM_ERROR",
      `Gemini API returned HTTP ${lastStatus}`,
    );
  }

  if (
    lastWasTimeout ||
    performance.now() - start >=
      totalTimeoutMs - MIN_REQUEST_TIME_MS
  ) {
    throw new HttpError(
      504,
      "AI_TIMEOUT",
      "Gemini did not respond before the configured deadline",
    );
  }

  throw new HttpError(
    502,
    "AI_UNAVAILABLE",
    `Could not reach AI API (attempts=${attempts.join(",")}): ${lastError || "Unknown network error"}`,
  );
}
