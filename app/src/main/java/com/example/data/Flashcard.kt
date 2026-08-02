package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exam: String,
    val subject: String,
    val chapter: String = "General",
    val question: String,
    val answer: String,
    val nextReviewDateMillis: Long = System.currentTimeMillis(),
    val easeFactor: Float = 2.5f,
    val intervalDays: Int = 0,
    val repetitions: Int = 0
)
