package com.example.ielts.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ListeningResultReviewDto(
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
    @SerializedName("sectionNumber")
    val sectionNumber: Int = 1,
    @SerializedName("evidenceQuote")
    val evidenceQuote: String? = null,
    @SerializedName("timestampSeconds")
    val timestampSeconds: Int = 0,
    @SerializedName("timestampStr")
    val timestampStr: String = "00:00",
    @SerializedName("imageResId")
    val imageResId: Int? = null,
    @SerializedName("imageTitle")
    val imageTitle: String? = null
)

data class ListeningSubmissionResultDto(
    @SerializedName("testId")
    val testId: String? = null,
    @SerializedName("score")
    val score: Int = 0,
    @SerializedName("totalQuestions")
    val totalQuestions: Int = 40,
    @SerializedName("bandEstimate")
    val bandEstimate: Double? = null,
    @SerializedName("bandScore")
    val bandScore: Double? = null,
    @SerializedName("durationSpent")
    val durationSpent: Int = 0,
    @SerializedName("results")
    val results: List<ListeningResultReviewDto> = emptyList()
) {
    fun resolvedBand(): Double = bandScore ?: bandEstimate ?: 0.0
}

