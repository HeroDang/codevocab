package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.DayProgress
import com.group20.codevocab.data.local.dao.FlashcardProgressDao
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity

class FlashcardProgressRepository(private val dao: FlashcardProgressDao) {

    suspend fun getByVocabId(vocabId: String): FlashcardProgressEntity? = dao.getByVocabId(vocabId)

    suspend fun getByModule(moduleId: String): List<FlashcardProgressEntity> = dao.getByModule(moduleId)

    suspend fun insert(flashcard: FlashcardProgressEntity): Long = dao.insert(flashcard)

    suspend fun insertAll(list: List<FlashcardProgressEntity>) = dao.insertAll(list)

    suspend fun update(flashcard: FlashcardProgressEntity) = dao.update(flashcard)

    suspend fun markKnown(vocabId: String, moduleId: String, isKnown: Boolean) {
        val entity = FlashcardProgressEntity(
            vocabId = vocabId,
            moduleId = moduleId,
            isKnown = isKnown,
            lastReviewed = System.currentTimeMillis()
        )
        dao.insertOrUpdate(entity)
    }

    suspend fun ensureFlashcardForVocab(vocabId: String, moduleId: String) {
        val exists = dao.getByVocabId(vocabId)
        if (exists == null) {
            dao.insert(FlashcardProgressEntity(vocabId = vocabId, moduleId = moduleId))
        }
    }

    suspend fun countByModule(moduleId: String): Int = dao.countByModule(moduleId)
    suspend fun countKnownByModule(moduleId: String): Int = dao.countKnownByModule(moduleId)

    // ✅ Thêm các hàm hỗ trợ Stats
    suspend fun getTotalKnownWords(): Int = dao.getTotalKnownWords()
    suspend fun getWeeklyProgress(): List<DayProgress> = dao.getWeeklyProgress()
}