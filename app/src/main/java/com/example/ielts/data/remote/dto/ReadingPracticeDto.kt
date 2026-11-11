package com.example.ielts.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs cho Phòng thi Reading (Passage, Câu hỏi, Nộp bài, Kết quả)
 */

// 1. Chi tiết đề thi đầy đủ
data class ReadingTestDetailDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("durationMinutes")
    val durationMinutes: Int = 60,
    @SerializedName("totalQuestions")
    val totalQuestions: Int = 40,
    @SerializedName("passages")
    val passages: List<PassageDto> = emptyList(),
    @SerializedName("questions")
    val questions: List<QuestionDetailDto> = emptyList()
)

data class PassageDto(
    @SerializedName("passageNumber")
    val passageNumber: Int = 1,
    @SerializedName("title")
    val title: String = "",
    @SerializedName("subtitle")
    val subtitle: String? = null,
    @SerializedName("content")
    val content: String = "",
    @SerializedName("wordCount")
    val wordCount: Int = 0
)

data class QuestionDetailDto(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("position")
    val position: Int = 1,
    @SerializedName("type")
    val type: String = "MULTIPLE_CHOICE", // MULTIPLE_CHOICE, TRUE_FALSE, FILL_IN_BLANK
    @SerializedName("prompt")
    val prompt: String = "",
    @SerializedName("options")
    val options: Any? = null,
    @SerializedName("passageNumber")
    val passageNumber: Int = 1,
    @SerializedName("groupInstruction")
    val groupInstruction: String? = null,
    @SerializedName("lineRef")
    val lineRef: String? = null
)

// 2. Yêu cầu nộp bài
data class SubmitReadingRequest(
    @SerializedName("testId")
    val testId: String,
    @SerializedName("durationSpentSeconds")
    val durationSpentSeconds: Int,
    @SerializedName("answers")
    val answers: List<UserAnswerDto>
)

data class UserAnswerDto(
    @SerializedName("questionId")
    val questionId: String,
    @SerializedName("answer")
    val answer: String
)

// 3. Kết quả chấm điểm trả về
data class ReadingSubmissionResultDto(
    @SerializedName("submissionId")
    val submissionId: String? = null,
    @SerializedName("testId")
    val testId: String? = null,
    @SerializedName("score")
    val score: Int = 0,
    @SerializedName("correctCount")
    val correctCount: Int = 0,
    @SerializedName("totalQuestions")
    val totalQuestions: Int = 40,
    @SerializedName("bandEstimate")
    val bandEstimate: Double? = null,
    @SerializedName("bandScore")
    val bandScore: Double? = null,
    @SerializedName("durationSpentSeconds")
    val durationSpentSeconds: Int = 0,
    @SerializedName("breakdown")
    val breakdown: List<QuestionResultReviewDto> = emptyList(),
    @SerializedName("results")
    val results: List<QuestionResultReviewDto> = emptyList()
) {
    fun resolvedBand(): Double = bandScore ?: bandEstimate ?: 0.0
    fun resolvedBreakdown(): List<QuestionResultReviewDto> = if (results.isNotEmpty()) results else breakdown
}

data class QuestionResultReviewDto(
    @SerializedName("questionId")
    val questionId: String,
    @SerializedName("position")
    val position: Int,
    @SerializedName("userAnswer")
    val userAnswer: String? = null,
    @SerializedName("correctAnswer")
    val correctAnswer: String,
    @SerializedName("isCorrect")
    val isCorrect: Boolean,
    @SerializedName("explanation")
    val explanation: String? = null,
    @SerializedName("prompt")
    val prompt: String? = null,
    @SerializedName("passageNumber")
    val passageNumber: Int = 1,
    @SerializedName("evidenceQuote")
    val evidenceQuote: String? = null
)
