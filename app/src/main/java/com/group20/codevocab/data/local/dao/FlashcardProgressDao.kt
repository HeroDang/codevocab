package com.group20.codevocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.OnConflictStrategy
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity

@Dao
interface FlashcardProgressDao {

    @Query("SELECT * FROM flashcard_progress WHERE id = :id")
    suspend fun getById(id: Int): FlashcardProgressEntity?

    @Query("SELECT * FROM flashcard_progress WHERE vocab_id = :vocabId LIMIT 1")
    suspend fun getByVocabId(vocabId: Int): FlashcardProgressEntity?

    // Get flashcards for a module (join vocabulary -> module_id)
    @Query("""
        SELECT f.* FROM flashcard_progress f
        JOIN vocabulary v ON v.id = f.vocab_id
        WHERE v.module_id = :moduleId
        ORDER BY
            CASE WHEN f.last_reviewed IS NULL THEN 0 ELSE 1 END, 
            f.last_reviewed ASC
    """)
    suspend fun getByModule(moduleId: Int): List<FlashcardProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flashcard: FlashcardProgressEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<FlashcardProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: FlashcardProgressEntity)

    @Update
    suspend fun update(flashcard: FlashcardProgressEntity)

    @Query("UPDATE flashcard_progress SET is_known = :isKnown, last_reviewed = :ts WHERE id = :id")
    suspend fun markKnown(id: Int, isKnown: Boolean, ts: Long)

    @Query("DELETE FROM flashcard_progress WHERE id = :id")
    suspend fun deleteById(id: Int)

    // Statistics
    @Query("""
        SELECT COUNT(*) FROM flashcard_progress f
        JOIN vocabulary v ON v.id = f.vocab_id
        WHERE v.module_id = :moduleId
    """)
    suspend fun countByModule(moduleId: Int): Int

    @Query("""
        SELECT COUNT(*) FROM flashcard_progress f
        JOIN vocabulary v ON v.id = f.vocab_id
        WHERE v.module_id = :moduleId AND f.is_known = 1
    """)
    suspend fun countKnownByModule(moduleId: Int): Int
}