package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.data.repository.FlashcardProgressRepository
import com.group20.codevocab.data.repository.VocabularyRepository
import kotlinx.coroutines.launch

class FlashcardViewModel(
    private val vocabRepo: VocabularyRepository,
    private val flashRepo: FlashcardProgressRepository
) : ViewModel() {

    private val _knowCount = MutableLiveData(0)
    val knowCount: LiveData<Int> = _knowCount

    private val _hardCount = MutableLiveData(0)
    val hardCount: LiveData<Int> = _hardCount

    private val _reviewCount = MutableLiveData(0)
    val reviewCount: LiveData<Int> = _reviewCount

    private val _vocabList = MutableLiveData<List<Pair<WordEntity, FlashcardProgressEntity?>>>()
    val vocabList: LiveData<List<Pair<WordEntity, FlashcardProgressEntity?>>> get() = _vocabList

    fun loadVocabWithProgress(moduleId: String) {
        viewModelScope.launch {
//            val vocabList = vocabRepo.getVocabByModule(moduleId)
//
//            // Tạo flashcard nếu vocab chưa có
//            vocabList.forEach { v ->
//                flashRepo.ensureFlashcardForVocab(v.id, moduleId = v.moduleId)
//            }

//            val flashList = flashRepo.getByModule(moduleId)
//            val combined = vocabList.map { vocab ->
//                val flash = flashList.find { it.vocabId == vocab.id }
//                vocab to flash
//            }
//
//            _vocabList.postValue(combined)
        }
    }

    fun updateStatus(vocabId: String, moduleId: String, status: FlashcardStatus) {
//        val vocabIdInt = vocabId.toIntOrNull() ?: return
//        val moduleIdInt = moduleId.toIntOrNull() ?: 0

        viewModelScope.launch {
            // isKnown = true nếu chọn Know, false nếu chọn Hard/Review
            val isKnown = status == FlashcardStatus.KNOW
//            flashRepo.markKnown(vocabId, moduleId, isKnown)

            when (status) {
                FlashcardStatus.KNOW -> _knowCount.value = (_knowCount.value ?: 0) + 1
                FlashcardStatus.HARD -> _hardCount.value = (_hardCount.value ?: 0) + 1
                FlashcardStatus.REVIEW -> _reviewCount.value = (_reviewCount.value ?: 0) + 1
            }
        }
    }

    enum class FlashcardStatus {
        KNOW, HARD, REVIEW
    }

    /**
     * Đánh dấu 1 flashcard là đã học (isKnown = true/false)
     */
    fun markKnown(flashcardId: Int, isKnown: Boolean, moduleId: String) {
        viewModelScope.launch {
//            flashRepo.markKnown(flashcardId, moduleId, isKnown)
//            loadVocabWithProgress(moduleId)
        }
    }

    /**
     * Tính % tiến độ học của module (đã học / tổng)
     */
    fun getProgressPercent(moduleId: String, callback: (Int) -> Unit) {
//        viewModelScope.launch {
//            val total = flashRepo.countByModule(moduleId)
//            val known = flashRepo.countKnownByModule(moduleId)
//            val percent = if (total > 0) (known * 100 / total) else 0
//            callback(percent)
//        }
    }
}