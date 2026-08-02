package com.example.data

import kotlinx.coroutines.flow.Flow

class FlashcardRepository(private val flashcardDao: FlashcardDao) {
    fun getFlashcardsForExam(exam: String): Flow<List<Flashcard>> = flashcardDao.getFlashcardsForExam(exam)
    suspend fun insertFlashcard(flashcard: Flashcard) = flashcardDao.insertFlashcard(flashcard)
    suspend fun updateFlashcard(flashcard: Flashcard) = flashcardDao.updateFlashcard(flashcard)
    suspend fun deleteFlashcard(flashcard: Flashcard) = flashcardDao.deleteFlashcard(flashcard)
}
