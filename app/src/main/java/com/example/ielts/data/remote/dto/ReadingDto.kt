package com.example.ielts.data.remote.dto

data class UserProgressDto(
    val submissionId: String? = null,
    val highestScore: Float? = null,
    val bandEstimate: Float? = null,
    val lastAttemptAt: String? = null
)

data class ReadingTestListItemDto(
    val id: String,
    val title: String,
    val summary: String? = null,
    val difficulty: String = "Academic",
    val durationMinutes: Int = 60,
    val totalQuestions: Int = 40,
    val passageTitles: List<String> = emptyList(),
    val userProgress: UserProgressDto? = null
)
