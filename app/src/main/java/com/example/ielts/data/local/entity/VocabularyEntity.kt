package com.example.ielts.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabularies")
data class VocabularyEntity(
    @PrimaryKey
    val id: String,
    val word: String,
    val phonetic: String,
    val meaningVi: String,
    val example: String,
    val srsLevel: Int = 0,
    val nextReviewDate: Long = 0L
)
