package com.group20.codevocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.OnConflictStrategy
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity

@Dao
interface FlashcardProgressDao {

    @Query("SELECT * FROM flashcard_progress WHERE vocab_id = :vocabId LIMIT 1")
    suspend fun getByVocabId(vocabId: String): FlashcardProgressEntity?

    @Query("""
        SELECT f.* FROM flashcard_progress f
        WHERE f.module_id = :moduleId
        ORDER BY
            CASE WHEN f.last_reviewed IS NULL THEN 0 ELSE 1 END, 
            f.last_reviewed ASC
    """)
    suspend fun getByModule(moduleId: String): List<FlashcardProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flashcard: FlashcardProgressEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<FlashcardProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: FlashcardProgressEntity)

    @Update
    suspend fun update(flashcard: FlashcardProgressEntity)

    @Query("DELETE FROM flashcard_progress WHERE vocab_id = :vocabId")
    suspend fun deleteByVocabId(vocabId: String)

    @Query("SELECT COUNT(*) FROM flashcard_progress WHERE module_id = :moduleId")
    suspend fun countByModule(moduleId: String): Int

    @Query("SELECT COUNT(*) FROM flashcard_progress WHERE module_id = :moduleId AND is_known = 1")
    suspend fun countKnownByModule(moduleId: String): Int

    // ✅ Lấy Module ID theo thứ tự học gần nhất
    @Query("SELECT module_id FROM flashcard_progress GROUP BY module_id ORDER BY MAX(last_reviewed) DESC")
    suspend fun getInProgressModuleIds(): List<String>
}