package com.example.ielts.domain.model

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val currentBand: Float = 0.0f,
    val targetBand: Float = 6.5f,
    val streak: Int = 0
)

data class Test(
    val id: String,
    val title: String,
    val skill: SkillType,
    val durationMinutes: Int,
    val passageOrAudioUrl: String? = null
)

enum class SkillType {
    READING, LISTENING, WRITING, SPEAKING
}

data class Vocabulary(
    val id: String,
    val word: String,
    val phonetic: String,
    val meaningVi: String,
    val example: String,
    val srsLevel: Int = 0,
    val nextReviewDate: Long = 0L
)
