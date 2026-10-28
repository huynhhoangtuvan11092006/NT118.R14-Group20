package com.example.ielts.data.remote.dto

data class WritingEvaluationResponse(
    val overallBand: Float,
    val taskAchievement: CriteriaFeedback,
    val coherenceCohesion: CriteriaFeedback,
    val lexicalResource: CriteriaFeedback,
    val grammaticalAccuracy: CriteriaFeedback,
    val grammarCorrections: List<GrammarCorrection>,
    val sampleRewrite: String? = null
)

data class CriteriaFeedback(
    val band: Float,
    val feedback: String
)

data class GrammarCorrection(
    val original: String,
    val corrected: String,
    val explanation: String
)
