package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_marks")
data class TestMark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exam: String,
    val testName: String,
    val dateMillis: Long,
    val score: Float,
    val maxScore: Float
)
