package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.WordDao
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.data.remote.ApiService
import com.group20.codevocab.model.WordItem
import com.group20.codevocab.model.mapper.toWordItem

class WordRepository(
    private val api: ApiService,
    private val wordDao: WordDao) {
    suspend fun insertWord(wordEntity: WordEntity) = wordDao.insert(wordEntity)
    suspend fun getAllWords() = wordDao.getAllWords()

    suspend fun getWordsRemote(subModuleId: String): List<WordItem> {
        return api.getWordsBySubmodule(subModuleId).map { it.toWordItem() }
    }
}
