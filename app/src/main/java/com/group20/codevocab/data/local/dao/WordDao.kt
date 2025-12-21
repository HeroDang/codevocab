package com.group20.codevocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.group20.codevocab.data.local.entity.WordEntity

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wordEntity: WordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<WordEntity>)

    @Query("SELECT * FROM words")
    suspend fun getAllWords(): List<WordEntity>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getWordById(id: String): WordEntity?
    
    @Query("SELECT * FROM words WHERE module_id = :moduleId")
    suspend fun getWordsByModule(moduleId: String): List<WordEntity>

    @Query("SELECT meaning_vi FROM words WHERE meaning_vi != :correctMeaning ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomMeaningsExcept(correctMeaning: String, limit: Int): List<String>
}