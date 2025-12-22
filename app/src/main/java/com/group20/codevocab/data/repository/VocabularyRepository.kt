package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.WordDao
import com.group20.codevocab.data.local.entity.WordEntity

class VocabularyRepository(private val wordDao: WordDao) {

    suspend fun getVocabByModule(moduleId: String): List<WordEntity> {
        return wordDao.getWordsByModule(moduleId)
    }

    suspend fun getVocabById(id: String): WordEntity? {
        return wordDao.getWordById(id)
    }

}