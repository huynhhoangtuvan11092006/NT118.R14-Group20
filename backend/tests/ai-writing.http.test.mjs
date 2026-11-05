import assert from "node:assert/strict";
import { after, before, test } from "node:test";

// No real Gemini API key and no DB connection is used by these route tests.
process.env.DATABASE_URL = "postgresql://bandup:test@127.0.0.1:5432/bandup?schema=public";
process.env.JWT_ACCESS_SECRET = "local_test_access_secret_never_for_production_012345";
process.env.JWT_REFRESH_SECRET = "local_test_refresh_secret_never_for_production_987654";
process.env.GEMINI_API_KEY = "mock-key-do-not-use";

const { app } = await import("../dist/app.js");
const { signAccessToken } = await import("../dist/lib/jwt.js");
const { prisma } = await import("../dist/config/database.js");
const originalFetch = globalThis.fetch;
let listener;
let baseUrl;
const essay = "I have study English for two years. Learning English is important because it opens many opportunities. I practise writing daily and I also read books to develop my skills.";
const evaluateBody = { taskType: "TASK_2", question: "Should children learn a second language at school? Discuss both views and give your opinion.", essay };
const fakeEvaluation = {
  criteria: {
    taskAchievementOrResponse: { score: 6, feedback: "Có quan điểm nhưng cần thêm giải thích và dẫn chứng.", evidence: "Learning English is important" },
    coherenceCohesion: { score: 6.5, feedback: "Có trình tự rõ nhưng liên kết cần chặt chẽ hơn.", evidence: "I practise writing daily" },
    lexicalResource: { score: 6, feedback: "Từ vựng phù hợp nhưng độ đa dạng còn hạn chế.", evidence: "many opportunities" },
    grammaticalRangeAccuracy: { score: 5.5, feedback: "Cần sửa thì hiện tại hoàn thành trong câu đầu.", evidence: "I have study English for two years." },
  },
  overallFeedback: "Bài viết có ý rõ nhưng quá ngắn và cần cải thiện thì động từ trong câu đầu tiên.",
  improvementPlan: ["Thêm một ví dụ để phát triển luận điểm chính."],
  grammarCorrections: [{ original: "I have study English for two years.", rewrite: "I have studied English for two years.", explanation: "Dùng quá khứ phân từ sau have." }],
};

before(async () => {
  globalThis.fetch = async (url, init) => {
    if (String(url).startsWith("https://generativelanguage.googleapis.com/")) {
      assert.equal(init.headers["x-goog-api-key"], "mock-key-do-not-use");
      return Response.json({ candidates: [{ content: { parts: [{ text: JSON.stringify(fakeEvaluation) }] } }] });
    }
    return originalFetch(url, init);
  };
  await new Promise((resolve) => { listener = app.listen(0, "127.0.0.1", resolve); });
  baseUrl = `http://127.0.0.1:${listener.address().port}`;
});

after(async () => {
  globalThis.fetch = originalFetch;
  await new Promise((resolve) => listener.close(resolve));
  await prisma.$disconnect();
});

test("HTTP Writing without JWT is 401 JSON", async () => {
  const res = await originalFetch(`${baseUrl}/ai/evaluate/writing`, { method: "POST", headers: { "content-type": "application/json" }, body: JSON.stringify(evaluateBody) });
  assert.equal(res.status, 401);
  const json = await res.json();
  assert.equal(json.error, "UNAUTHORIZED");
});

test("HTTP Writing with invalid payload is 400 JSON", async () => {
  const token = signAccessToken("693cd292-a1f0-4400-9efa-b78b8d6ae7e9");
  const res = await originalFetch(`${baseUrl}/ai/evaluate/writing`, { method: "POST", headers: { "content-type": "application/json", authorization: `Bearer ${token}` }, body: JSON.stringify({ ...evaluateBody, taskType: "OTHER" }) });
  assert.equal(res.status, 400);
  const json = await res.json();
  assert.equal(json.error, "VALIDATION_ERROR");
});

test("HTTP Writing with JWT returns structured 200 JSON and corrected sentence", async () => {
  const token = signAccessToken("693cd292-a1f0-4400-9efa-b78b8d6ae7e9");
  const res = await originalFetch(`${baseUrl}/ai/evaluate/writing`, { method: "POST", headers: { "content-type": "application/json", authorization: `Bearer ${token}` }, body: JSON.stringify(evaluateBody) });
  assert.equal(res.status, 200);
  assert.match(res.headers.get("content-type"), /application\/json/);
  const json = await res.json();
  assert.equal(json.estimatedBand, 6);
  assert.equal(json.grammarCorrections[0].rewrite, "I have studied English for two years.");
  assert.ok(!JSON.stringify(json).includes("mock-key-do-not-use"));
});
