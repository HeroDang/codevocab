package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.FlashcardDao
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity

class FlashcardProgressRepository(private val dao: FlashcardDao) {

    suspend fun getByVocabId(vocabId: Int): FlashcardProgressEntity? = dao.getByVocabId(vocabId)

    suspend fun getByModule(moduleId: Int): List<FlashcardProgressEntity> = dao.getByModule(moduleId)

    suspend fun insert(flashcard: FlashcardProgressEntity): Long = dao.insert(flashcard)

    suspend fun insertAll(list: List<FlashcardProgressEntity>) = dao.insertAll(list)

    suspend fun update(flashcard: FlashcardProgressEntity) = dao.update(flashcard)

//    suspend fun markKnown(id: Int, isKnown: Boolean, ts: Long) = dao.markKnown(id, isKnown, ts)
suspend fun markKnown(vocabId: Int, moduleId: Int, isKnown: Boolean) {
    val entity = FlashcardProgressEntity(
        vocabId = vocabId,
        moduleId = moduleId,
        isKnown = isKnown,
        lastReviewed = System.currentTimeMillis()
    )
    dao.insertOrUpdate(entity)
}

    suspend fun ensureFlashcardForVocab(vocabId: Int, moduleId: Int) {
        val exists = dao.getByVocabId(vocabId)
        if (exists == null) {
            dao.insert(FlashcardProgressEntity(vocabId = vocabId, moduleId = moduleId))
        }
    }

    suspend fun countByModule(moduleId: Int): Int = dao.countByModule(moduleId)
    suspend fun countKnownByModule(moduleId: Int): Int = dao.countKnownByModule(moduleId)
}