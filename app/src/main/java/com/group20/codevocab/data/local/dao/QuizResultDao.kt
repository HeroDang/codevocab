package com.group20.codevocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.group20.codevocab.data.local.entity.QuizResultEntity

@Dao
interface QuizResultDao {

    @Insert
    suspend fun insertResult(result: QuizResultEntity)

    @Query("SELECT * FROM quiz_results WHERE module_id = :moduleId ORDER BY created_at DESC")
    suspend fun getResultsByModule(moduleId: Int): List<QuizResultEntity>
}
