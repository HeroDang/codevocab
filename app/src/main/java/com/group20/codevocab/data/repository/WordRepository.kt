package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.WordDao
import com.group20.codevocab.data.local.entity.WordEntity

class WordRepository(private val wordDao: WordDao) {
    suspend fun insertWord(wordEntity: WordEntity) = wordDao.insert(wordEntity)
    suspend fun getAllWords() = wordDao.getAllWords()
}
