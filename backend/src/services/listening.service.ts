import { prisma } from "../config/database.js";
import { HttpError } from "../lib/http-error.js";
import { SubmissionStatus, TestStatus, TestType } from "@prisma/client";
import type { SubmitListeningInput } from "../validators/listening.validator.js";

/**
 * Bảng quy đổi điểm IELTS Listening chuẩn xác (Thang 9.0)
 * Theo đúng thang điểm khảo thí từ IDP & British Council
 */
export function calculateListeningBandScore(correctCount: number): number {
  if (correctCount >= 39) return 9.0;
  if (correctCount >= 37) return 8.5;
  if (correctCount >= 35) return 8.0;
  if (correctCount >= 32) return 7.5;
  if (correctCount >= 30) return 7.0;
  if (correctCount >= 26) return 6.5;
  if (correctCount >= 23) return 6.0;
  if (correctCount >= 18) return 5.5;
  if (correctCount >= 16) return 5.0;
  if (correctCount >= 13) return 4.5;
  if (correctCount >= 10) return 4.0;
  if (correctCount >= 8) return 3.5;
  if (correctCount >= 6) return 3.0;
  if (correctCount >= 4) return 2.5;
  if (correctCount >= 1) return 2.0;
  return 0.0;
}

export interface ListeningSectionItem {
  sectionNumber: number;
  title: string;
  audioUrl: string;
  durationSeconds: number;
  transcript: string;
}

export interface QuestionMeta {
  sectionNumber?: number;
  timestampStart?: number;
  timestampEnd?: number;
  distractor?: string;
  quote?: string;
}

function normalizeAnswer(ans: string | undefined | null): string {
  if (!ans) return "";
  return ans.trim().toUpperCase().replace(/[^A-Z0-9]/g, "");
}

function normalizeTextAnswer(ans: string | undefined | null): string {
  if (!ans) return "";
  return ans
    .trim()
    .toLowerCase()
    .replace(/[.,\/#!$%\^&\*;:{}=\-_`~()]/g, "")
    .replace(/\s+/g, " ");
}

/**
 * 1. Lấy danh sách các đề thi Listening
 */
export async function getListeningTests(userId?: string) {
  const tests = await prisma.test.findMany({
    where: {
      type: TestType.LISTENING,
      status: TestStatus.PUBLISHED,
    },
    include: {
      _count: {
        select: { questions: true },
      },
      submissions: userId
        ? {
            where: { userId, status: SubmissionStatus.GRADED },
            orderBy: { createdAt: "desc" },
            take: 1,
            select: {
              id: true,
              score: true,
              bandEstimate: true,
              createdAt: true,
            },
          }
        : false,
    },
    orderBy: { createdAt: "desc" },
  });

  return tests.map((t) => {
    let summary: string | null = null;
    let difficulty = "Standard IELTS";
    let sectionTitles: string[] = [];
    let audioUrl: string | null = null;
    let totalAudioDuration = 1800; // default 30 mins

    if (t.description) {
      try {
        const parsed = JSON.parse(t.description) as {
          summary?: string;
          difficulty?: string;
          audioUrl?: string;
          totalAudioDuration?: number;
          sections?: ListeningSectionItem[];
        };
        summary = parsed.summary ?? null;
        difficulty = parsed.difficulty ?? "Standard IELTS";
        audioUrl = parsed.audioUrl ?? null;
        totalAudioDuration = parsed.totalAudioDuration ?? 1800;
        sectionTitles = (parsed.sections ?? []).map((s) => s.title);
      } catch {
        summary = t.description;
      }
    }

    const latest = Array.isArray(t.submissions) && t.submissions.length > 0 ? t.submissions[0] : null;

    return {
      id: t.id,
      title: t.title,
      summary,
      difficulty,
      durationMinutes: t.durationMinutes ?? 35,
      totalAudioDuration,
      audioUrl,
      totalQuestions: t._count.questions > 0 ? t._count.questions : 40,
      sectionTitles,
      userProgress: latest
        ? {
            submissionId: latest.id,
            highestScore: latest.score?.toNumber() ?? null,
            bandEstimate: latest.bandEstimate?.toNumber() ?? null,
            lastAttemptAt: latest.createdAt.toISOString(),
          }
        : null,
    };
  });
}

/**
 * 2. Lấy chi tiết đề thi Listening (ẨN ĐÁP ÁN ĐÚNG KHI LÀM BÀI)
 */
export async function getListeningTestDetail(testId: string) {
  const test = await prisma.test.findUnique({
    where: { id: testId },
    include: {
      questions: {
        orderBy: { position: "asc" },
        select: {
          id: true,
          position: true,
          type: true,
          prompt: true,
          options: true,
          points: true,
          explanation: true, // Chứa metadata (timestamp, section)
        },
      },
    },
  });

  if (!test || test.type !== TestType.LISTENING) {
    throw new HttpError(404, "TEST_NOT_FOUND", "Không tìm thấy đề thi Listening");
  }

  let sections: ListeningSectionItem[] = [];
  let summary = "";
  let audioUrl = "";

  if (test.description) {
    try {
      const parsed = JSON.parse(test.description) as {
        summary?: string;
        audioUrl?: string;
        sections?: ListeningSectionItem[];
      };
      summary = parsed.summary ?? "";
      audioUrl = parsed.audioUrl ?? "";
      sections = (parsed.sections ?? []).map((sec) => ({
        sectionNumber: sec.sectionNumber,
        title: sec.title,
        audioUrl: sec.audioUrl,
        durationSeconds: sec.durationSeconds,
        transcript: sec.transcript ?? "",
      }));
    } catch {
      summary = test.description;
    }
  }

  // Parse questions ẩn answerKey
  const sanitizedQuestions = test.questions.map((q) => {
    let sectionNumber = 1;
    let timestampStart = 0;
    let timestampEnd = 0;

    if (q.position > 30) sectionNumber = 4;
    else if (q.position > 20) sectionNumber = 3;
    else if (q.position > 10) sectionNumber = 2;

    if (q.explanation) {
      try {
        const meta = JSON.parse(q.explanation) as QuestionMeta;
        if (meta.sectionNumber) sectionNumber = meta.sectionNumber;
        if (meta.timestampStart !== undefined) timestampStart = meta.timestampStart;
        if (meta.timestampEnd !== undefined) timestampEnd = meta.timestampEnd;
      } catch {
        // explanation là raw text
      }
    }

    return {
      id: q.id,
      position: q.position,
      type: q.type,
      prompt: q.prompt,
      options: q.options,
      points: q.points,
      sectionNumber,
      timestampStart,
      timestampEnd,
    };
  });

  return {
    id: test.id,
    title: test.title,
    summary,
    audioUrl,
    durationMinutes: test.durationMinutes ?? 35,
    sections,
    questions: sanitizedQuestions,
  };
}

/**
 * 3. Chấm điểm bài thi Listening
 */
export async function gradeListeningSubmission(userId: string | null, input: SubmitListeningInput) {
  const test = await prisma.test.findUnique({
    where: { id: input.testId },
    include: {
      questions: {
        orderBy: { position: "asc" },
      },
    },
  });

  if (!test || test.type !== TestType.LISTENING) {
    throw new HttpError(404, "TEST_NOT_FOUND", "Không tìm thấy đề thi Listening tương ứng");
  }

  let correctCount = 0;
  const totalQuestions = test.questions.length;
  const sectionCorrect: Record<number, number> = { 1: 0, 2: 0, 3: 0, 4: 0 };
  const sectionTotal: Record<number, number> = { 1: 0, 2: 0, 3: 0, 4: 0 };

  const results = test.questions.map((q) => {
    let sectionNumber = 1;
    let timestampStart = 0;
    let quote = "";
    let distractor = "";

    if (q.position > 30) sectionNumber = 4;
    else if (q.position > 20) sectionNumber = 3;
    else if (q.position > 10) sectionNumber = 2;

    if (q.explanation) {
      try {
        const meta = JSON.parse(q.explanation) as QuestionMeta;
        if (meta.sectionNumber) sectionNumber = meta.sectionNumber;
        if (meta.timestampStart !== undefined) timestampStart = meta.timestampStart;
        if (meta.quote) quote = meta.quote;
        if (meta.distractor) distractor = meta.distractor;
      } catch {
        distractor = q.explanation;
      }
    }

    sectionTotal[sectionNumber] = (sectionTotal[sectionNumber] || 0) + 1;

    const rawUserAnswer =
      input.answers[String(q.position)] ?? input.answers[q.position as unknown as string] ?? "";
    const answerKeyObj = q.answerKey as { value?: string; accepted?: string[] } | null;
    const correctValue = answerKeyObj?.value ?? "";
    const acceptedList: string[] = answerKeyObj?.accepted ?? [correctValue];

    let isCorrect = false;

    if (q.type === "MULTIPLE_CHOICE" || q.type === "TRUE_FALSE") {
      const userNorm = normalizeAnswer(rawUserAnswer);
      const correctNorm = normalizeAnswer(correctValue);
      isCorrect = userNorm !== "" && userNorm === correctNorm;
    } else {
      const userNormText = normalizeTextAnswer(rawUserAnswer);
      isCorrect =
        userNormText !== "" &&
        acceptedList.some((item) => normalizeTextAnswer(item) === userNormText);
    }

    if (isCorrect) {
      correctCount++;
      sectionCorrect[sectionNumber] = (sectionCorrect[sectionNumber] || 0) + 1;
    }

    return {
      position: q.position,
      sectionNumber,
      timestampStart,
      type: q.type,
      prompt: q.prompt,
      userAnswer: rawUserAnswer,
      correctAnswer: correctValue,
      acceptedAnswers: acceptedList,
      isCorrect,
      quote,
      distractor,
    };
  });

  const bandScore = calculateListeningBandScore(correctCount);
  const accuracy = totalQuestions > 0 ? Math.round((correctCount / totalQuestions) * 100) : 0;

  // Lưu kết quả vào DB nếu đã đăng nhập
  let submissionId: string | null = null;
  if (userId) {
    try {
      const created = await prisma.submission.create({
        data: {
          userId,
          testId: test.id,
          status: SubmissionStatus.GRADED,
          answers: input.answers,
          score: correctCount,
          maxScore: totalQuestions,
          bandEstimate: bandScore,
        },
      });
      submissionId = created.id;
    } catch (err) {
      console.warn("Could not save submission to DB:", err);
    }
  }

  // Đọc sections để trả về transcript phục vụ chữa đề
  let sections: ListeningSectionItem[] = [];
  if (test.description) {
    try {
      const parsed = JSON.parse(test.description) as { sections?: ListeningSectionItem[] };
      sections = parsed.sections ?? [];
    } catch {
      // ignore
    }
  }

  return {
    submissionId,
    testId: test.id,
    title: test.title,
    timeSpentSeconds: input.timeSpentSeconds ?? 0,
    correctCount,
    totalQuestions,
    bandScore,
    accuracy,
    sectionBreakdown: {
      section1: { correct: sectionCorrect[1] ?? 0, total: sectionTotal[1] ?? 10 },
      section2: { correct: sectionCorrect[2] ?? 0, total: sectionTotal[2] ?? 10 },
      section3: { correct: sectionCorrect[3] ?? 0, total: sectionTotal[3] ?? 10 },
      section4: { correct: sectionCorrect[4] ?? 0, total: sectionTotal[4] ?? 10 },
    },
    sections,
    results,
  };
}

/**
 * 4. Xem lại chi tiết bài nộp Listening đã làm
 */
export async function getListeningSubmissionDetail(submissionId: string, userId?: string) {
  const submission = await prisma.submission.findUnique({
    where: { id: submissionId },
    include: {
      test: {
        include: {
          questions: {
            orderBy: { position: "asc" },
          },
        },
      },
    },
  });

  if (!submission) {
    throw new HttpError(404, "SUBMISSION_NOT_FOUND", "Không tìm thấy bài nộp tương ứng");
  }

  if (userId && submission.userId !== userId) {
    throw new HttpError(403, "FORBIDDEN", "Bạn không có quyền xem bài làm này");
  }

  const userAnswers = (submission.answers ?? {}) as Record<string, string>;
  const sectionCorrect: Record<number, number> = { 1: 0, 2: 0, 3: 0, 4: 0 };
  const sectionTotal: Record<number, number> = { 1: 0, 2: 0, 3: 0, 4: 0 };

  const results = submission.test.questions.map((q) => {
    let sectionNumber = 1;
    let timestampStart = 0;
    let quote = "";
    let distractor = "";

    if (q.position > 30) sectionNumber = 4;
    else if (q.position > 20) sectionNumber = 3;
    else if (q.position > 10) sectionNumber = 2;

    if (q.explanation) {
      try {
        const meta = JSON.parse(q.explanation) as QuestionMeta;
        if (meta.sectionNumber) sectionNumber = meta.sectionNumber;
        if (meta.timestampStart !== undefined) timestampStart = meta.timestampStart;
        if (meta.quote) quote = meta.quote;
        if (meta.distractor) distractor = meta.distractor;
      } catch {
        distractor = q.explanation;
      }
    }

    sectionTotal[sectionNumber] = (sectionTotal[sectionNumber] || 0) + 1;

    const rawUserAnswer = userAnswers[String(q.position)] ?? userAnswers[q.position as unknown as string] ?? "";
    const answerKeyObj = q.answerKey as { value?: string; accepted?: string[] } | null;
    const correctValue = answerKeyObj?.value ?? "";
    const acceptedList: string[] = answerKeyObj?.accepted ?? [correctValue];

    let isCorrect = false;
    if (q.type === "MULTIPLE_CHOICE" || q.type === "TRUE_FALSE") {
      const userNorm = normalizeAnswer(rawUserAnswer);
      const correctNorm = normalizeAnswer(correctValue);
      isCorrect = userNorm !== "" && userNorm === correctNorm;
    } else {
      const userNormText = normalizeTextAnswer(rawUserAnswer);
      isCorrect =
        userNormText !== "" &&
        acceptedList.some((item) => normalizeTextAnswer(item) === userNormText);
    }

    if (isCorrect) {
      sectionCorrect[sectionNumber] = (sectionCorrect[sectionNumber] || 0) + 1;
    }

    return {
      position: q.position,
      sectionNumber,
      timestampStart,
      type: q.type,
      prompt: q.prompt,
      userAnswer: rawUserAnswer,
      correctAnswer: correctValue,
      isCorrect,
      quote,
      distractor,
    };
  });

  let sections: ListeningSectionItem[] = [];
  let audioUrl = "";
  if (submission.test.description) {
    try {
      const parsed = JSON.parse(submission.test.description) as {
        audioUrl?: string;
        sections?: ListeningSectionItem[];
      };
      sections = parsed.sections ?? [];
      audioUrl = parsed.audioUrl ?? "";
    } catch {
      // ignore
    }
  }

  return {
    submissionId: submission.id,
    testId: submission.testId,
    title: submission.test.title,
    audioUrl,
    score: submission.score?.toNumber() ?? 0,
    maxScore: submission.maxScore?.toNumber() ?? 40,
    bandEstimate: submission.bandEstimate?.toNumber() ?? 0,
    submittedAt: submission.createdAt.toISOString(),
    sectionBreakdown: {
      section1: { correct: sectionCorrect[1] ?? 0, total: sectionTotal[1] ?? 10 },
      section2: { correct: sectionCorrect[2] ?? 0, total: sectionTotal[2] ?? 10 },
      section3: { correct: sectionCorrect[3] ?? 0, total: sectionTotal[3] ?? 10 },
      section4: { correct: sectionCorrect[4] ?? 0, total: sectionTotal[4] ?? 10 },
    },
    sections,
    results,
  };
}

