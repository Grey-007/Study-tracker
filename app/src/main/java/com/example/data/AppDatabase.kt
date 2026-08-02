package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Topic::class, TestMark::class, Flashcard::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun testMarkDao(): TestMarkDao
    abstract fun flashcardDao(): FlashcardDao
    
    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE topics ADD COLUMN notes TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE topics ADD COLUMN isDailyGoal INTEGER NOT NULL DEFAULT 0")
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `flashcards` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `exam` TEXT NOT NULL, 
                        `subject` TEXT NOT NULL, 
                        `question` TEXT NOT NULL, 
                        `answer` TEXT NOT NULL, 
                        `nextReviewDateMillis` INTEGER NOT NULL, 
                        `easeFactor` REAL NOT NULL, 
                        `intervalDays` INTEGER NOT NULL, 
                        `repetitions` INTEGER NOT NULL
                    )
                """)
            }
        }
    }
}
