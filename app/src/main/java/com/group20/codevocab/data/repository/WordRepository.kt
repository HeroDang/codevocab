package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.WordDao
import com.group20.codevocab.model.Word

class WordRepository(private val wordDao: WordDao) {
    suspend fun insertWord(word: Word) = wordDao.insert(word)
    suspend fun getAllWords() = wordDao.getAllWords()
}
