# BandUp — Gemini IELTS Writing Evaluation

**Target backend:** `E:\cc\NT118.R14-Group20\backend` (Node 22, TypeScript, Express 5, Zod 4)

This feature adds two aliases for the same **JWT-protected** endpoint:

- `POST http://localhost:3000/ai/evaluate/writing`
- `POST http://localhost:3000/api/v1/ai/evaluate/writing`

## 1. Environment and startup (Windows PowerShell)

From **your existing repository's `backend` folder** (not a standalone patch directory):

```powershell
cd E:\cc\NT118.R14-Group20\backend
notepad .env
```

Preserve **all existing** PostgreSQL, JWT, and other settings; add only:

```dotenv
GEMINI_API_KEY=YOUR_ACTUAL_AI_STUDIO_KEY
GEMINI_MODEL=gemini-3.8-flash
GEMINI_TIMEOUT_MS=8500
```

Get the key at https://aistudio.google.com/api-keys. Never expose it in Android, Postman, Git or screenshots. The Gemini chat Pro subscription and API billing are separate.

```powershell
npm run typecheck
npm run lint
npm run test:writing
npm run dev
```

**No new npm dependency or Prisma migration** for this task. If `node_modules` is absent, install from the official registry: `npm install --registry=https://registry.npmjs.org/ --no-audit --no-fund`.

## 2. Postman

**Step A:** `POST http://localhost:3000/auth/login` using your existing test account. Save the **accessToken** (do not share it).

**Step B:** Create an HTTP request:

- Method: `POST`
- URL: `http://localhost:3000/ai/evaluate/writing` (without the word POST inside the URL field).
- Authorization → **Bearer Token** → insert `accessToken` only.
- Body → **raw** → **JSON**:

```json
{
  "taskType": "TASK_2",
  "question": "Some people believe online learning is more effective than studying in a traditional classroom. Discuss both views and give your opinion.",
  "essay": "Some people believe that studying online is more useful than going to a classroom. I think both methods have benefits, but I prefer learning in person. Online lessons help students save time because they do not need to travel. Students can also watch recorded lessons again when they do not understand something. However, learning online requires good self-discipline. In a traditional classroom, teachers can answer questions immediately. Students also practise communicating with their classmates, which can improve teamwork. In my opinion, face-to-face lessons are better for students who need regular guidance. Nevertheless, online learning can be useful for people who work full time or live far from schools. Schools should therefore offer a combination of both approaches so students can choose an appropriate way to study."
}
```

**Academic Task 1** requires `sourceMaterial` (verifiable text description of the chart/table/process) as well as `taskType: "ACADEMIC_TASK_1"`. This text-only MVP does not process chart images; do not expect accurate Task Achievement scoring without supplying the source data.

A valid **200** response has:

- `criteria.taskAchievementOrResponse` (Task Achievement on Task 1; Task Response on Task 2)
- `criteria.coherenceCohesion`
- `criteria.lexicalResource`
- `criteria.grammaticalRangeAccuracy`
- Each criterion: `score`, `feedback`, `evidence`
- `estimatedBand`, `overallFeedback`, `improvementPlan`, `grammarCorrections: [{original,rewrite,explanation}]`
- `wordCount`, `warnings`, `model`, `latencyMs`, `disclaimer`

Example *shape only* (illustrative, **not a real assessment**):

```json
{
  "taskType": "TASK_2",
  "criterion1Name": "Task Response",
  "criteria": {
    "taskAchievementOrResponse": {"score": 6, "feedback": "Luận điểm cần dẫn chứng cụ thể hơn.", "evidence": "I think both methods have benefits"},
    "coherenceCohesion": {"score": 6.5, "feedback": "Các ý được sắp xếp khá mạch lạc.", "evidence": "However, learning online requires good self-discipline."},
    "lexicalResource": {"score": 6, "feedback": "Từ vựng tương đối rõ nhưng còn lặp.", "evidence": "online learning"},
    "grammaticalRangeAccuracy": {"score": 6.5, "feedback": "Có câu phức, nhưng cần đa dạng hơn.", "evidence": "which can improve teamwork"}
  },
  "estimatedBand": 6.5,
  "overallFeedback": "Bài viết có lập trường rõ, nên phát triển ví dụ.",
  "improvementPlan": ["Phát triển một ví dụ cụ thể cho mỗi luận điểm."],
  "grammarCorrections": [],
  "wordCount": 125,
  "warnings": ["Bài viết có 125 từ, dưới mức 250 từ của IELTS Writing Task 2."],
  "model": "gemini-3.8-flash",
  "latencyMs": 3500,
  "disclaimer": "Điểm IELTS Writing ước tính bởi AI cho mục đích luyện tập, không phải điểm chấm chính thức."
}
```

**Errors also return JSON**: `400` invalid input; `401` missing/expired JWT; `429` Gemini quota; `502` upstream invalid JSON/invalid schema/other upstream failure; `503` missing key or permission; `504` deadline exceeded. No fabricated bands are returned in provider error cases.

## 3. Timing / quality Acceptance Criteria

- Provider call is limited to **8.5 seconds** by default, leaving a small budget for server overhead. This does **not guarantee** the complete response always finishes under 10 s (network/queueing are outside the app's control).
- JSON Schema is sent in Gemini's REST `generationConfig.responseFormat.text.schema`, with `mimeType: APPLICATION_JSON` (current API). After receipt, the response is `JSON.parse`d and independently validated by **Zod**. Invalid results return a typed JSON error, never unparsed model text or invented scores.
- The server computes the mean of the four criteria and rounds to the nearest half band; it checks quoted essay evidence and excludes corrections without a verbatim original. Scores remain **estimates**.
- To validate model accuracy, gather scored human-reference essays and calculate error/consistency on each criterion. No model can be asserted to match trained IELTS examiners without a benchmark.
- On a real machine with API access, measure 20–50 representative Writing evaluations: p50 and p95 time, non-2xx error rate, schema validation rejection rate, and score deviation vs instructor review. A fixed 10 s deadline will cause timeouts under load.

## 4. Tests

```powershell
npm run test:writing
```

Runs build and **mocked Gemini service + HTTP route** suites for schema, success, band aggregation, grammar rewrites, fabricated evidence, invalid JSON, rejected bands, 429, missing key, timeout, JWT 401 and input 400. Does not call Gemini or PostgreSQL, does not incur fees. Live tests still require a real API key and server.

## 5. References

- Gemini JSON Schema / REST: https://ai.google.dev/gemini-api/docs/generate-content/structured-output
- IELTS criteria: https://ielts.org/take-a-test/preparation-resources/writing-test-resources

## 6. Scope limits

This implementation does **not** add a Writing database table, grading history or image upload; those can be separate tasks. Do not expose this endpoint without rate limiting and per-user quotas in production. Avoid logging student essays and never store keys in mobile source code.
