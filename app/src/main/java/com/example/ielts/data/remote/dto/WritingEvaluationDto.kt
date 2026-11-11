package com.example.ielts.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Request gửi lên Backend AI để chấm bài Writing
 */
data class WritingEvaluationRequest(
    @SerializedName("taskType")
    val taskType: String, // ACADEMIC_TASK_1, GENERAL_TASK_1, TASK_2
    @SerializedName("question")
    val question: String,
    @SerializedName("essay")
    val essay: String,
    @SerializedName("sourceMaterial")
    val sourceMaterial: String? = null
)

/**
 * Response chấm điểm Writing từ Gemini AI thông qua Backend
 */
data class WritingEvaluationResponse(
    @SerializedName("taskType")
    val taskType: String = "TASK_2",
    @SerializedName("criterion1Name")
    val criterion1Name: String = "Task Response",
    @SerializedName("criteria")
    val criteria: WritingCriteriaBreakdown = WritingCriteriaBreakdown(),
    @SerializedName("estimatedBand")
    val estimatedBand: Double = 0.0,
    @SerializedName("overallFeedback")
    val overallFeedback: String = "",
    @SerializedName("improvementPlan")
    val improvementPlan: List<String> = emptyList(),
    @SerializedName("grammarCorrections")
    val grammarCorrections: List<GrammarCorrectionItem> = emptyList(),
    @SerializedName("wordCount")
    val wordCount: Int = 0
)

data class WritingCriteriaBreakdown(
    @SerializedName("taskAchievementOrResponse")
    val taskAchievementOrResponse: CriterionDetail = CriterionDetail(),
    @SerializedName("coherenceCohesion")
    val coherenceCohesion: CriterionDetail = CriterionDetail(),
    @SerializedName("lexicalResource")
    val lexicalResource: CriterionDetail = CriterionDetail(),
    @SerializedName("grammaticalRangeAccuracy")
    val grammaticalRangeAccuracy: CriterionDetail = CriterionDetail()
)

data class CriterionDetail(
    @SerializedName("score")
    val score: Double = 0.0,
    @SerializedName("feedback")
    val feedback: String = "",
    @SerializedName("evidence")
    val evidence: String = ""
)

data class GrammarCorrectionItem(
    @SerializedName("original")
    val original: String = "",
    @SerializedName("rewrite")
    val rewrite: String = "",
    @SerializedName("explanation")
    val explanation: String = ""
)
