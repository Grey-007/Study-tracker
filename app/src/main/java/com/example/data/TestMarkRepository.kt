package com.example.data

import kotlinx.coroutines.flow.Flow

class TestMarkRepository(private val testMarkDao: TestMarkDao) {
    fun getTestMarksByExam(exam: String): Flow<List<TestMark>> = testMarkDao.getTestMarksByExam(exam)

    suspend fun insertTestMark(testMark: TestMark) {
        testMarkDao.insertTestMark(testMark)
    }
}
