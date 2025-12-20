package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.VocabDao
import com.group20.codevocab.data.local.entity.VocabularyEntity
import com.group20.codevocab.model.WordItem
import com.group20.codevocab.model.mapper.toWordItem

class VocabularyRepository(private val vocabDao: VocabDao) {

    suspend fun getVocabByModule(moduleId: Int): List<VocabularyEntity> {
        return vocabDao.getVocabByModule(moduleId)
    }

    suspend fun getVocabById(id: Int): VocabularyEntity {
        return vocabDao.getVocabById(id)
    }

}
