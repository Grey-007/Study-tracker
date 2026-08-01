package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class Topic(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exam: String,
    val subject: String,
    val name: String,
    val isCompleted: Boolean = false
)
