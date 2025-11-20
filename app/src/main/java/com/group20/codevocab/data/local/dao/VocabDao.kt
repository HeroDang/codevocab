package com.group20.codevocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.group20.codevocab.data.local.entity.VocabularyEntity

@Dao
interface VocabDao {

    @Query("SELECT * FROM vocabulary WHERE module_id = :moduleId")
    suspend fun getVocabByModule(moduleId: Int): List<VocabularyEntity>

    @Query("SELECT * FROM vocabulary WHERE id = :id")
    suspend fun getVocabById(id: Int): VocabularyEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vocabList: List<VocabularyEntity>)

    @Query("SELECT meaning_vi FROM vocabulary WHERE meaning_vi != :correct ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomMeaningsExcept(correct: String, count: Int): List<String>
}
