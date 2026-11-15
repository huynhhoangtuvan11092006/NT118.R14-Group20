package com.example.ielts.data.remote.dto

data class ListeningTestListItemDto(
    val id: String,
    val title: String,
    val summary: String? = null,
    val difficulty: String = "Standard IELTS",
    val durationMinutes: Int = 35,
    val totalAudioDuration: Int = 1800,
    val totalQuestions: Int = 40,
    val sectionTitles: List<String> = emptyList(),
    val audioUrl: String? = null,
    val userProgress: UserProgressDto? = null
)

data class ListeningSectionDto(
    val sectionNumber: Int,
    val title: String,
    val audioUrl: String,
    val durationSeconds: Int,
    val transcript: String? = null
)

data class ListeningQuestionDetailDto(
    val id: String,
    val position: Int,
    val type: String,
    val prompt: String,
    val options: List<String>? = null,
    val points: Int = 1,
    val sectionNumber: Int = 1,
    val timestampStart: Int = 0,
    val timestampEnd: Int = 0,
    val imageResId: Int? = null,
    val imageTitle: String? = null
)

data class ListeningTestDetailDto(
    val id: String,
    val title: String,
    val summary: String? = null,
    val audioUrl: String? = null,
    val durationMinutes: Int = 35,
    val sections: List<ListeningSectionDto> = emptyList(),
    val questions: List<ListeningQuestionDetailDto> = emptyList()
)

data class SubmitListeningRequest(
    val testId: String,
    val answers: Map<String, String>,
    val timeSpentSeconds: Int = 0
)

data class ListeningResultItemDto(
    val position: Int,
    val sectionNumber: Int = 1,
    val timestampStart: Int = 0,
    val type: String,
    val prompt: String,
    val userAnswer: String = "",
    val correctAnswer: String = "",
    val acceptedAnswers: List<String> = emptyList(),
    val isCorrect: Boolean = false,
    val quote: String = "",
    val distractor: String = ""
)

data class SectionScoreDto(
    val correct: Int = 0,
    val total: Int = 10
)

data class SectionBreakdownDto(
    val section1: SectionScoreDto? = null,
    val section2: SectionScoreDto? = null,
    val section3: SectionScoreDto? = null,
    val section4: SectionScoreDto? = null
)

data class SubmitListeningResponse(
    val submissionId: String? = null,
    val testId: String,
    val title: String,
    val timeSpentSeconds: Int = 0,
    val correctCount: Int = 0,
    val totalQuestions: Int = 40,
    val bandScore: Double = 0.0,
    val accuracy: Int = 0,
    val sectionBreakdown: SectionBreakdownDto? = null,
    val sections: List<ListeningSectionDto> = emptyList(),
    val results: List<ListeningResultItemDto> = emptyList()
)

