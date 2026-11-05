
import assert from "node:assert/strict";
import { test } from "node:test";

// No real Gemini key or network required.
// Tests import the compiled JavaScript from dist/.
process.env.DATABASE_URL =
  "postgresql://bandup:test@127.0.0.1:5432/bandup?schema=public";
process.env.JWT_ACCESS_SECRET =
  "local_test_access_secret_never_for_production_012345";
process.env.JWT_REFRESH_SECRET =
  "local_test_refresh_secret_never_for_production_987654";

const { evaluateWriting } = await import(
  "../dist/services/ai_writing.service.js"
);

const { writingEvaluationInputSchema } = await import(
  "../dist/validators/writing.validator.js"
);

const {
  buildWritingPrompt,
  writingEvaluationJsonSchema,
  IELTS_WRITING_SYSTEM_PROMPT,
} = await import(
  "../dist/prompts/ielts_writing_prompt.js"
);

const essay =
  "I have study English for two years. Learning English is important because it opens many opportunities. I practise writing daily and I also read books to develop my skills.";

const input = {
  taskType: "TASK_2",
  question:
    "Some people believe that learning languages is essential for everyone. Discuss both views and give your opinion.",
  essay,
};

const template = {
  criteria: {
    taskAchievementOrResponse: {
      score: 6,
      feedback:
        "Luận điểm rõ nhưng dẫn chứng cần cụ thể hơn.",
      evidence: "Learning English is important",
    },

    coherenceCohesion: {
      score: 6.5,
      feedback:
        "Các câu liên kết được nhưng đoạn văn còn ngắn.",
      evidence: "I practise writing daily",
    },

    lexicalResource: {
      score: 6,
      feedback:
        "Từ vựng đủ rõ nhưng chưa thật phong phú.",
      evidence: "many opportunities",
    },

    grammaticalRangeAccuracy: {
      score: 5.5,
      feedback:
        "Thì hiện tại hoàn thành dùng chưa đúng.",
      evidence:
        "I have study English for two years.",
    },
  },

  overallFeedback:
    "Bạn có ý tưởng rõ nhưng cần viết dài hơn và tăng độ chính xác về ngữ pháp.",

  improvementPlan: [
    "Bổ sung ví dụ thực tế để phát triển luận điểm.",
    "Ôn lại hiện tại hoàn thành với have/has.",
  ],

  grammarCorrections: [
    {
      original:
        "I have study English for two years.",
      rewrite:
        "I have studied English for two years.",
      explanation:
        "Động từ sau have phải dùng dạng quá khứ phân từ.",
    },
  ],
};

// Mock Gemini response.
// No external API request is made.
const success = (payload) => async () =>
  new Response(
    JSON.stringify({
      candidates: [
        {
          content: {
            parts: [
              {
                text: JSON.stringify(payload),
              },
            ],
          },
        },
      ],
    }),
    { status: 200 },
  );

const opts = {
  apiKey: "mock-key-not-real",
  model: "gemini-3.8-flash",
  fetchImpl: success(template),
};

// ===================================================
// TEST 1 - JSON Schema
// ===================================================

test(
  "configured Gemini JSON Schema has four criteria and grammar correction shape",
  () => {
    assert.equal(
      writingEvaluationJsonSchema.type,
      "object",
    );

    assert.equal(
      Object.keys(
        writingEvaluationJsonSchema.properties
          .criteria.properties,
      ).length,
      4,
    );

    assert.ok(
      writingEvaluationJsonSchema.properties
        .grammarCorrections.items.required
        .includes("rewrite"),
    );
  },
);

// ===================================================
// TEST 2 - Prompt
// ===================================================

test(
  "Task 2 prompt uses Task Response and text is included as untrusted",
  () => {
    const prompt = buildWritingPrompt(input);

    assert.match(
      prompt,
      /Task 2 — Task Response/,
    );

    assert.ok(
      prompt.includes(essay),
    );
  },
);

// ===================================================
// TEST 3 - Task 1 requires reference material
// ===================================================

test(
  "Academic Task 1 needs chart context",
  () => {
    assert.equal(
      writingEvaluationInputSchema.safeParse({
        ...input,
        taskType: "ACADEMIC_TASK_1",
      }).success,
      false,
    );

    assert.equal(
      writingEvaluationInputSchema.safeParse({
        ...input,
        taskType: "ACADEMIC_TASK_1",
        sourceMaterial:
          "The bar chart compares three years, with the highest value in 2020.",
      }).success,
      true,
    );
  },
);

// ===================================================
// TEST 4 - Successful evaluation
// ===================================================

test(
  "Gemini result is validated and overall band is computed by server",
  async () => {
    const result = await evaluateWriting(
      input,
      opts,
    );

    assert.equal(
      result.estimatedBand,
      6,
    );

    assert.equal(
      result.criterion1Name,
      "Task Response",
    );

    assert.equal(
      result.grammarCorrections.length,
      1,
    );

    assert.equal(
      result.grammarCorrections[0].rewrite,
      "I have studied English for two years.",
    );

    assert.equal(
      result.model,
      "gemini-3.8-flash",
    );

    assert.ok(
      result.latencyMs >= 0,
    );

    assert.ok(
      result.warnings.some((warning) =>
        warning.includes("250"),
      ),
    );
  },
);

// ===================================================
// TEST 5 - Gemini request configuration
// ===================================================

test(
  "Request config uses responseFormat JSON schema and server key header",
  async () => {
    await evaluateWriting(input, {
      apiKey: "mock-secret-123",
      model: "gemini-3.8-flash",

      fetchImpl: async (url, config) => {
        assert.match(
          url,
          /gemini-3\.8-flash:generateContent$/,
        );

        assert.equal(
          config.headers["x-goog-api-key"],
          "mock-secret-123",
        );

        const body = JSON.parse(
          config.body,
        );

        assert.equal(
          body.generationConfig.responseFormat
            .text.mimeType,
          "APPLICATION_JSON",
        );

        assert.equal(
          body.generationConfig.responseFormat
            .text.schema.type,
          "object",
        );

        assert.equal(
          body.generationConfig.thinkingConfig
            .thinkingLevel,
          "low",
        );

        return success(template)();
      },
    });
  },
);

// ===================================================
// TEST 6 - Fake grammar correction
// ===================================================

test(
  "Forged original sentence is not returned as a correction",
  async () => {
    const forged = {
      ...template,

      grammarCorrections: [
        {
          original: "Not from essay.",
          rewrite: "Different.",
          explanation:
            "Câu bịa đặt không nên hiển thị.",
        },
      ],
    };

    const result = await evaluateWriting(
      input,
      {
        ...opts,
        fetchImpl: success(forged),
      },
    );

    assert.equal(
      result.grammarCorrections.length,
      0,
    );
  },
);

// ===================================================
// TEST 7 - Invalid JSON
// ===================================================

test(
  "Malformed Gemini text is rejected with typed 502 error",
  async () => {
    const fetchImpl = async () =>
      new Response(
        JSON.stringify({
          candidates: [
            {
              content: {
                parts: [
                  {
                    text: "{broken",
                  },
                ],
              },
            },
          ],
        }),
        { status: 200 },
      );

    await assert.rejects(
      evaluateWriting(input, {
        ...opts,
        fetchImpl,
      }),

      (error) =>
        error.status === 502 &&
        error.code === "AI_INVALID_RESPONSE",
    );
  },
);

// ===================================================
// TEST 8 - Invalid score
// ===================================================

test(
  "Out-of-band scoring is rejected",
  async () => {
    const invalid =
      structuredClone(template);

    invalid.criteria.lexicalResource.score =
      6.3;

    await assert.rejects(
      evaluateWriting(input, {
        ...opts,
        fetchImpl: success(invalid),
      }),

      (error) =>
        error.status === 502 &&
        error.code === "AI_INVALID_RESPONSE",
    );
  },
);

// ===================================================
// TEST 9 - Invented evidence
// NEW BEHAVIOR: return 200 with empty evidence.
// ===================================================

test(
  "Invented essay evidence is omitted and produces a warning",
  async () => {
    const invalid =
      structuredClone(template);

    invalid.criteria.lexicalResource.evidence =
      "Invented evidence from model";

    const result = await evaluateWriting(
      input,
      {
        ...opts,
        fetchImpl: success(invalid),
      },
    );

    // Không được hiển thị evidence bịa đặt.
    assert.equal(
      result.criteria.lexicalResource.evidence,
      "",
    );

    // Evidence hợp lệ khác vẫn giữ nguyên.
    assert.equal(
      result.criteria.taskAchievementOrResponse
        .evidence,
      "Learning English is important",
    );

    // Phải có warning cho tiêu chí bị lỗi.
    assert.ok(
      result.warnings.some((warning) =>
        warning.includes("lexicalResource"),
      ),
    );

    // Bài chấm vẫn được trả kết quả.
    assert.equal(
      result.estimatedBand,
      6,
    );
  },
);

// ===================================================
// TEST 10 - Extra quotation marks
// ===================================================

test(
  "Evidence surrounded by added quotes is normalized",
  async () => {
    const quoted =
      structuredClone(template);

    quoted.criteria.lexicalResource.evidence =
      '"many opportunities"';

    const result = await evaluateWriting(
      input,
      {
        ...opts,
        fetchImpl: success(quoted),
      },
    );

    assert.equal(
      result.criteria.lexicalResource.evidence,
      "many opportunities",
    );

    assert.ok(
      !result.warnings.some((warning) =>
        warning.includes("lexicalResource"),
      ),
    );
  },
);

// ===================================================
// TEST 11 - Whitespace normalization
// ===================================================

test(
  "Evidence with extra whitespace maps back to essay text",
  async () => {
    const spaced =
      structuredClone(template);

    spaced.criteria.coherenceCohesion.evidence =
      "I practise   writing daily";

    const result = await evaluateWriting(
      input,
      {
        ...opts,
        fetchImpl: success(spaced),
      },
    );

    assert.equal(
      result.criteria.coherenceCohesion.evidence,
      "I practise writing daily",
    );

    assert.ok(
      !result.warnings.some((warning) =>
        warning.includes("coherenceCohesion"),
      ),
    );
  },
);

// ===================================================
// TEST 12 - Multiple invalid evidence entries
// ===================================================

test(
  "Multiple unverified evidence excerpts are removed independently",
  async () => {
    const invalid =
      structuredClone(template);

    invalid.criteria.lexicalResource.evidence =
      "Evidence invented by the model";

    invalid.criteria.grammaticalRangeAccuracy.evidence =
      "Another made-up citation";

    const result = await evaluateWriting(
      input,
      {
        ...opts,
        fetchImpl: success(invalid),
      },
    );

    assert.equal(
      result.criteria.lexicalResource.evidence,
      "",
    );

    assert.equal(
      result.criteria.grammaticalRangeAccuracy
        .evidence,
      "",
    );

    assert.equal(
      result.criteria.taskAchievementOrResponse
        .evidence,
      "Learning English is important",
    );

    assert.equal(
      result.criteria.coherenceCohesion.evidence,
      "I practise writing daily",
    );

    assert.ok(
      result.warnings.some((warning) =>
        warning.includes("lexicalResource"),
      ),
    );

    assert.ok(
      result.warnings.some((warning) =>
        warning.includes(
          "grammaticalRangeAccuracy",
        ),
      ),
    );
  },
);

// ===================================================
// TEST 13 - Rate limit
// ===================================================

test(
  "HTTP 429 returns a typed rate-limit error",
  async () => {
    await assert.rejects(
      evaluateWriting(input, {
        ...opts,

        fetchImpl: async () =>
          new Response(
            "quota exceeded",
            { status: 429 },
          ),
      }),

      (error) =>
        error.status === 429 &&
        error.code === "AI_RATE_LIMITED",
    );
  },
);

// ===================================================
// TEST 14 - Timeout
// ===================================================

test(
  "Mock timeout gives a typed 504 error",
  async () => {
    const timeout = async () => {
      throw new DOMException(
        "timeout",
        "TimeoutError",
      );
    };

    await assert.rejects(
      evaluateWriting(input, {
        ...opts,
        fetchImpl: timeout,
      }),

      (error) =>
        error.status === 504 &&
        error.code === "AI_TIMEOUT",
    );
  },
);

// ===================================================
// TEST 15 - Missing API key
// ===================================================

test(
  "No real key returns 503 before trying network",
  async () => {
    await assert.rejects(
      evaluateWriting(input, {
        ...opts,
        apiKey: "",
      }),

      (error) =>
        error.status === 503 &&
        error.code === "AI_NOT_CONFIGURED",
    );
  },
);

// ===================================================
// Regression dataset - 269-word Writing Task 2
// ===================================================

const regressionEssay269 = [
  "Over the past few years, working from home has become a common option for many employees. Some people argue that it is better than working in a traditional office. I partly agree with this view because remote work offers flexibility, although offices still provide important benefits.",

  "One major advantage of working from home is that employees can manage their time more efficiently. They do not have to commute every morning, which allows them to spend more time with family or exercise before starting work. For example, someone who normally travel for two hours a day could use that time to prepare meals or learn a new skill. As a result, remote workers may feel less stressed and become more productive. In addition, companies can reduce expenses on electricity and office space.",

  "However, remote work also creates challenges. Communication through messages and video calls is not always as effective as speaking face to face. When colleagues work in different locations, misunderstandings can happen and some tasks take longer to complete. New employees may also find it difficult to build relationships with their teammates. Furthermore, working at home can be distracting, especially if a person share a small room with other family members. In such situations, an office offers a more professional environment and clearer boundaries between work and personal life.",

  "In conclusion, I believe working from home is a valuable choice because it saves time and offers more freedom. Nevertheless, it should not completely replace office work. A hybrid arrangement, in which employees work remotely on some days and meet colleagues in person on others, may provide the best balance.",
].join("\n\n");

// ===================================================
// TEST 16 - Grammar audit instructions
// ===================================================

test(
  "Prompt mandates a full grammar audit and correct criterion classification",
  () => {
    assert.match(
      IELTS_WRITING_SYSTEM_PROMPT,
      /EACH sentence/,
    );

    assert.match(
      IELTS_WRITING_SYSTEM_PROMPT,
      /relative clauses/,
    );

    assert.match(
      IELTS_WRITING_SYSTEM_PROMPT,
      /Subject-verb agreement and verb conjugation belong to Grammatical Range/,
    );

    assert.match(
      IELTS_WRITING_SYSTEM_PROMPT,
      /not Lexical Resource/,
    );

    assert.match(
      IELTS_WRITING_SYSTEM_PROMPT,
      /not.*optional style improvements/i,
    );
  },
);

// ===================================================
// TEST 17 - Two planted grammar errors
// ===================================================

test(
  "Both planted grammar corrections survive source validation",
  async () => {
    const firstOriginal =
      "For example, someone who normally travel for two hours a day could use that time to prepare meals or learn a new skill.";

    const secondOriginal =
      "Furthermore, working at home can be distracting, especially if a person share a small room with other family members.";

    assert.ok(
      regressionEssay269.includes(firstOriginal),
    );

    assert.ok(
      regressionEssay269.includes(secondOriginal),
    );

    assert.equal(
      regressionEssay269.trim().split(/\s+/u).length,
      269,
    );

    const both = [
      {
        original: firstOriginal,

        rewrite:
          "For example, someone who normally travels for two hours a day could use that time to prepare meals or learn a new skill.",

        explanation:
          "Someone là chủ ngữ số ít, nên travel phải thành travels.",
      },

      {
        original: secondOriginal,

        rewrite:
          "Furthermore, working at home can be distracting, especially if a person shares a small room with other family members.",

        explanation:
          "A person là chủ ngữ số ít, nên share phải thành shares.",
      },
    ];

    const mocked =
      structuredClone(template);

    mocked.criteria.taskAchievementOrResponse.evidence =
      "I partly agree with this view";

    mocked.criteria.coherenceCohesion.evidence =
      "However, remote work also creates challenges.";

    mocked.criteria.lexicalResource.evidence =
      "A hybrid arrangement";

    mocked.criteria.grammaticalRangeAccuracy.evidence =
      secondOriginal;

    mocked.grammarCorrections = both;

    const result = await evaluateWriting(
      {
        taskType: "TASK_2",

        question:
          "Some people believe that working from home is better than working in an office. To what extent do you agree or disagree?",

        essay: regressionEssay269,
      },

      {
        ...opts,
        fetchImpl: success(mocked),
      },
    );

    assert.equal(
      result.wordCount,
      269,
    );

    assert.equal(
      result.grammarCorrections.length,
      2,
    );

    assert.deepEqual(
      result.grammarCorrections.map(
        (correction) => correction.rewrite,
      ),

      both.map(
        (correction) => correction.rewrite,
      ),
    );

    assert.equal(
      result.warnings.length,
      0,
    );
  },
);

// ===================================================
// TEST 18 - Duplicate grammar correction
// ===================================================

test(
  "Repeated grammar correction is deduplicated",
  async () => {
    const duplicate =
      structuredClone(template);

    duplicate.grammarCorrections = [
      template.grammarCorrections[0],
      template.grammarCorrections[0],
    ];

    const result = await evaluateWriting(
      input,
      {
        ...opts,
        fetchImpl: success(duplicate),
      },
    );

    assert.equal(
      result.grammarCorrections.length,
      1,
    );
  },
);
