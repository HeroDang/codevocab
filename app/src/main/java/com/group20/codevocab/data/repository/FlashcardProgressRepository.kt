package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.FlashcardProgressDao
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity

class FlashcardProgressRepository(private val dao: FlashcardProgressDao) {

    suspend fun getByVocabId(vocabId: String): FlashcardProgressEntity? = dao.getByVocabId(vocabId)

    suspend fun getByModule(moduleId: String): List<FlashcardProgressEntity> = dao.getByModule(moduleId)

    suspend fun insert(flashcard: FlashcardProgressEntity): Long = dao.insert(flashcard)

    suspend fun insertAll(list: List<FlashcardProgressEntity>) = dao.insertAll(list)

    suspend fun update(flashcard: FlashcardProgressEntity) = dao.update(flashcard)

    suspend fun markKnown(flashcardId: Int, moduleId: String, isKnown: Boolean) {
        // Option 1: Update directly by ID if available
        dao.markKnown(flashcardId, isKnown, System.currentTimeMillis())
        
        // Option 2 (Old logic was creating new entity, potentially buggy if not handling ID):
        // Retaining Option 1 as it's safer given markKnown exists in DAO
    }

    suspend fun ensureFlashcardForVocab(vocabId: String, moduleId: String) {
        val exists = dao.getByVocabId(vocabId)
        if (exists == null) {
            dao.insert(FlashcardProgressEntity(vocabId = vocabId, moduleId = moduleId))
        }
    }

    suspend fun countByModule(moduleId: String): Int = dao.countByModule(moduleId)
    suspend fun countKnownByModule(moduleId: String): Int = dao.countKnownByModule(moduleId)
}