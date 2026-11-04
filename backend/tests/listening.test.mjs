import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { test } from "node:test";

test("Listening routes are registered in app.ts", () => {
  const app = readFileSync(new URL("../src/app.ts", import.meta.url), "utf8");
  assert.ok(app.includes('app.use("/tests/listening", listeningTestRouter)'));
  assert.ok(app.includes('app.use("/submissions/listening", listeningSubmissionRouter)'));
  assert.ok(app.includes('app.use("/api/v1/tests/listening", listeningTestRouter)'));
  assert.ok(app.includes('app.use("/api/v1/submissions/listening", listeningSubmissionRouter)'));
});

test("Cambridge 18 Listening Test 1 JSON data is valid and complete", () => {
  const rawData = readFileSync(new URL("../src/data/listening_cambridge18_test1.json", import.meta.url), "utf8");
  const data = JSON.parse(rawData);

  assert.equal(data.title, "Cambridge IELTS 18 - Listening Test 1");
  assert.equal(data.sections.length, 4, "Must have exactly 4 sections");
  assert.equal(data.questions.length, 40, "Must have exactly 40 questions");
  assert.ok(data.audioUrl, "Audio URL must be present");

  // Verify all 4 sections have audioUrl and transcript
  for (const sec of data.sections) {
    assert.ok(sec.sectionNumber >= 1 && sec.sectionNumber <= 4);
    assert.ok(sec.title.length > 0);
    assert.ok(sec.audioUrl.length > 0);
    assert.ok(sec.transcript.length > 0);
  }

  // Verify all 40 questions have position 1-40, type, prompt, answerKey and explanation
  for (let i = 1; i <= 40; i++) {
    const q = data.questions.find((item) => item.position === i);
    assert.ok(q, `Question ${i} must exist`);
    assert.ok(["FILL_IN_BLANK", "MULTIPLE_CHOICE"].includes(q.type));
    assert.ok(q.prompt.length > 0);
    assert.ok(q.answerKey && q.answerKey.value);
    assert.ok(q.explanation);

    const parsedExp = JSON.parse(q.explanation);
    assert.ok(parsedExp.sectionNumber >= 1 && parsedExp.sectionNumber <= 4);
    assert.ok(typeof parsedExp.timestampStart === "number");
    assert.ok(parsedExp.quote.length > 0);
  }
});

test("calculateListeningBandScore accurately converts correct answers to IELTS Band", async () => {
  const { calculateListeningBandScore } = await import("../dist/services/listening.service.js");

  assert.equal(calculateListeningBandScore(40), 9.0);
  assert.equal(calculateListeningBandScore(39), 9.0);
  assert.equal(calculateListeningBandScore(38), 8.5);
  assert.equal(calculateListeningBandScore(37), 8.5);
  assert.equal(calculateListeningBandScore(36), 8.0);
  assert.equal(calculateListeningBandScore(35), 8.0);
  assert.equal(calculateListeningBandScore(34), 7.5);
  assert.equal(calculateListeningBandScore(33), 7.5);
  assert.equal(calculateListeningBandScore(32), 7.5);
  assert.equal(calculateListeningBandScore(31), 7.0);
  assert.equal(calculateListeningBandScore(30), 7.0);
  assert.equal(calculateListeningBandScore(27), 6.5);
  assert.equal(calculateListeningBandScore(23), 6.0);
  assert.equal(calculateListeningBandScore(18), 5.5);
  assert.equal(calculateListeningBandScore(16), 5.0);
  assert.equal(calculateListeningBandScore(13), 4.5);
  assert.equal(calculateListeningBandScore(10), 4.0);
  assert.equal(calculateListeningBandScore(0), 0.0);
});

