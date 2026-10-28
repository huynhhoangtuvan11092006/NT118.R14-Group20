package com.example.ielts.data.local.entity

data class VocabularyEntity(
    val id: String,
    val word: String,
    val phonetic: String,
    val meaningVi: String,
    val example: String,
    val srsLevel: Int = 0,
    val nextReviewDate: Long = 0L
)
