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
    val sourceMaterial: String? = null,
    @SerializedName("targetBand")
    val targetBand: Double? = null
)

/**
 * Response chấm điểm Writing từ AI thông qua Backend
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
    @SerializedName("modelEssay")
    val modelEssay: String? = null,
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

data class AiRoadmapRequest(
    @SerializedName("currentBand")
    val currentBand: Double,
    @SerializedName("targetBand")
    val targetBand: Double,
    @SerializedName("studyGoal")
    val studyGoal: String,
    @SerializedName("dailyMinutes")
    val dailyMinutes: Int,
    @SerializedName("countdownLabel")
    val countdownLabel: String,
    @SerializedName("focusSkills")
    val focusSkills: List<String>,
    @SerializedName("completedLessons")
    val completedLessons: Int,
    @SerializedName("totalLessons")
    val totalLessons: Int
)

data class AiRoadmapResponse(
    @SerializedName("strategicAdvice")
    val strategicAdvice: String = "",
    @SerializedName("phase1Title")
    val phase1Title: String = "Nền tảng",
    @SerializedName("phase1Desc")
    val phase1Desc: String = "",
    @SerializedName("phase2Title")
    val phase2Title: String = "Bứt phá kỹ năng",
    @SerializedName("phase2Desc")
    val phase2Desc: String = "",
    @SerializedName("phase3Title")
    val phase3Title: String = "Đỉnh cao",
    @SerializedName("phase3Desc")
    val phase3Desc: String = "",
    @SerializedName("model")
    val model: String = "AI"
)

