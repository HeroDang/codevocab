package com.group20.codevocab.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.group20.codevocab.model.Word

@Dao
interface WordDao {
    @Insert
    suspend fun insert(word: Word)

    @Query("SELECT * FROM Word")
    suspend fun getAllWords(): List<Word>
}
