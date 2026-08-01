package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TestMarkDao {
    @Query("SELECT * FROM test_marks WHERE exam = :exam ORDER BY dateMillis ASC")
    fun getTestMarksByExam(exam: String): Flow<List<TestMark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestMark(testMark: TestMark)
}
