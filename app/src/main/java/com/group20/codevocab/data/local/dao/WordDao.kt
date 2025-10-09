package com.group20.codevocab.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.group20.codevocab.data.local.entity.WordEntity

@Dao
interface WordDao {
    @Insert
    suspend fun insert(wordEntity: WordEntity)

    @Query("SELECT * FROM WordEntity")
    suspend fun getAllWords(): List<WordEntity>
}
