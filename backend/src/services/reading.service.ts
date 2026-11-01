import { prisma } from "../config/database.js";
import { HttpError } from "../lib/http-error.js";
import { SubmissionStatus, TestStatus, TestType } from "@prisma/client";
import type { SubmitReadingInput } from "../validators/reading.validator.js";

/**
 * Bảng quy đổi điểm IELTS Academic Reading chuẩn xác (Thang 9.0)
 * Theo đúng bảng quy chuẩn từ IDP / British Council
 */
export function calculateReadingBandScore(correctCount: number): number {
  if (correctCount >= 39) return 9.0;
  if (correctCount >= 37) return 8.5;
  if (correctCount >= 35) return 8.0;
  if (correctCount >= 33) return 7.5;
  if (correctCount >= 30) return 7.0;
  if (correctCount >= 27) return 6.5;
  if (correctCount >= 23) return 6.0;
  if (correctCount >= 20) return 5.5;
  if (correctCount >= 16) return 5.0;
  if (correctCount >= 13) return 4.5;
  if (correctCount >= 10) return 4.0;
  if (correctCount >= 7) return 3.5;
  if (correctCount >= 5) return 3.0;
  if (correctCount >= 3) return 2.5;
  if (correctCount >= 1) return 2.0;
  return 0.0;
}

export interface PassageItem {
  passageNumber: number;
  title: string;
  subtitle?: string;
  content: string;
}

/**
 * 1. Lấy danh sách các đề thi Reading
 */
export async function getReadingTests(userId?: string) {
  const tests = await prisma.test.findMany({
    where: {
      type: TestType.READING,
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
    let difficulty = "Academic";
    let passageTitles: string[] = [];

    if (t.description) {
      try {
        const parsed = JSON.parse(t.description) as {
          summary?: string;
          difficulty?: string;
          passages?: PassageItem[];
        };
        summary = parsed.summary ?? null;
        difficulty = parsed.difficulty ?? "Academic";
        passageTitles = (parsed.passages ?? []).map((p) => p.title);
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
      durationMinutes: t.durationMinutes ?? 60,
      totalQuestions: t._count.questions > 0 ? t._count.questions : 40,
      passageTitles,
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
 * 2. Lấy chi tiết đề thi Reading (Bao gồm 3 bài đọc & 40 câu hỏi - ẨN ĐÁP ÁN ĐÚNG)
 */
export async function getReadingTestDetail(testId: string) {
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
          // BẢO MẬT: Tuyệt đối không select answerKey và explanation ở đây!
        },
      },
    },
  });

  if (!test || test.type !== TestType.READING) {
    throw new HttpError(404, "TEST_NOT_FOUND", "Không tìm thấy đề thi Reading tương ứng");
  }

  let passages: PassageItem[] = [];
  let summary = "";
  let difficulty = "Academic";

  if (test.description) {
    try {
      const parsed = JSON.parse(test.description) as {
        summary?: string;
        difficulty?: string;
        passages?: PassageItem[];
      };
      passages = parsed.passages ?? [];
      summary = parsed.summary ?? "";
      difficulty = parsed.difficulty ?? "Academic";
    } catch {
      summary = test.description;
    }
  }

  return {
    id: test.id,
    title: test.title,
    summary,
    difficulty,
    durationMinutes: test.durationMinutes ?? 60,
    totalQuestions: test.questions.length,
    passages,
    questions: test.questions,
  };
}

/**
 * Chuẩn hóa câu trả lời dạng trắc nghiệm A/B/C/D hoặc True/False
 */
function normalizeAnswer(ans: unknown): string {
  if (ans === null || ans === undefined) return "";
  const str = String(ans).trim();
  // Nếu là trắc nghiệm dạng "A. Option name" -> lấy "A"
  if (/^[A-Da-d][.\s]/.test(str)) {
    return (str[0] ?? "").toUpperCase();
  }
  return str.toUpperCase();
}

/**
 * Chuẩn hóa câu trả lời điền từ
 */
function normalizeTextAnswer(ans: unknown): string {
  if (ans === null || ans === undefined) return "";
  return String(ans).trim().toLowerCase().replace(/\s+/g, " ");
}

/**
 * 3. Chấm điểm bài nộp Reading tự động & Lưu vào Database
 */
export async function gradeReadingSubmission(userId: string | null, input: SubmitReadingInput) {
  const test = await prisma.test.findUnique({
    where: { id: input.testId },
    include: {
      questions: {
        orderBy: { position: "asc" },
      },
    },
  });

  if (!test) {
    throw new HttpError(404, "TEST_NOT_FOUND", "Không tìm thấy đề thi tương ứng");
  }

  const userAnswers = input.answers;
  let correctCount = 0;
  const totalQuestions = test.questions.length;

  const results = test.questions.map((q) => {
    // Lấy câu trả lời người dùng gửi lên theo position (dạng string hoặc int)
    const rawUserAnswer = userAnswers[String(q.position)] ?? userAnswers[q.position as unknown as string] ?? "";
    const answerKeyObj = q.answerKey as { value?: string; accepted?: string[] } | null;
    const correctValue = answerKeyObj?.value ?? "";
    const acceptedList: string[] = answerKeyObj?.accepted ?? [correctValue];

    let isCorrect = false;

    if (q.type === "TRUE_FALSE" || q.type === "MULTIPLE_CHOICE") {
      const userNorm = normalizeAnswer(rawUserAnswer);
      const correctNorm = normalizeAnswer(correctValue);
      isCorrect = userNorm !== "" && userNorm === correctNorm;
    } else {
      // FILL_IN_BLANK hoặc SHORT_ANSWER
      const userNormText = normalizeTextAnswer(rawUserAnswer);
      isCorrect = userNormText !== "" && acceptedList.some((item) => normalizeTextAnswer(item) === userNormText);
    }

    if (isCorrect) correctCount++;

    return {
      position: q.position,
      type: q.type,
      prompt: q.prompt,
      userAnswer: rawUserAnswer,
      correctAnswer: correctValue,
      isCorrect,
      explanation: q.explanation ?? "",
    };
  });

  const bandScore = calculateReadingBandScore(correctCount);
  const accuracy = totalQuestions > 0 ? Math.round((correctCount / totalQuestions) * 1000) / 10 : 0;

  let submissionId = "local-submission";

  // Nếu người dùng đã đăng nhập, lưu kết quả vào database
  if (userId) {
    const submission = await prisma.submission.create({
      data: {
        userId,
        testId: test.id,
        status: SubmissionStatus.GRADED,
        answers: userAnswers,
        score: correctCount,
        maxScore: totalQuestions,
        bandEstimate: bandScore,
        feedback: {
          accuracy,
          correctCount,
          totalQuestions,
        },
        submittedAt: new Date(),
      },
    });
    submissionId = submission.id;
  }

  return {
    submissionId,
    testId: test.id,
    title: test.title,
    correctCount,
    totalQuestions,
    bandScore,
    accuracy,
    results,
  };
}

/**
 * 4. Xem chi tiết bài nộp đã làm
 */
export async function getSubmissionDetail(submissionId: string, userId?: string) {
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

  const results = submission.test.questions.map((q) => {
    const rawUserAnswer = userAnswers[String(q.position)] ?? userAnswers[q.position as unknown as string] ?? "";
    const answerKeyObj = q.answerKey as { value?: string; accepted?: string[] } | null;
    const correctValue = answerKeyObj?.value ?? "";
    const acceptedList: string[] = answerKeyObj?.accepted ?? [correctValue];

    let isCorrect = false;
    if (q.type === "TRUE_FALSE" || q.type === "MULTIPLE_CHOICE") {
      const userNorm = normalizeAnswer(rawUserAnswer);
      const correctNorm = normalizeAnswer(correctValue);
      isCorrect = userNorm !== "" && userNorm === correctNorm;
    } else {
      const userNormText = normalizeTextAnswer(rawUserAnswer);
      isCorrect = userNormText !== "" && acceptedList.some((item) => normalizeTextAnswer(item) === userNormText);
    }

    return {
      position: q.position,
      type: q.type,
      prompt: q.prompt,
      userAnswer: rawUserAnswer,
      correctAnswer: correctValue,
      isCorrect,
      explanation: q.explanation ?? "",
    };
  });

  return {
    submissionId: submission.id,
    testId: submission.testId,
    title: submission.test.title,
    score: submission.score?.toNumber() ?? 0,
    maxScore: submission.maxScore?.toNumber() ?? 40,
    bandEstimate: submission.bandEstimate?.toNumber() ?? 0,
    submittedAt: submission.submittedAt?.toISOString() ?? submission.createdAt.toISOString(),
    results,
  };
}
