package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity
import com.group20.codevocab.data.local.entity.VocabularyEntity
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

    fun updateStatus(vocabId: String, moduleId: String, status: FlashcardStatus) {
        val vocabIdInt = vocabId.toIntOrNull() ?: return
        val moduleIdInt = moduleId.toIntOrNull() ?: 0

        viewModelScope.launch {
            // isKnown = true nếu chọn Know, false nếu chọn Hard/Review
            val isKnown = status == FlashcardStatus.KNOW
            flashRepo.markKnown(vocabIdInt, moduleIdInt, isKnown)

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

    // Các hàm cũ giữ lại nếu cần
    fun getProgressPercent(moduleId: Int, callback: (Int) -> Unit) {
        viewModelScope.launch {
            val total = flashRepo.countByModule(moduleId)
            val known = flashRepo.countKnownByModule(moduleId)
            val percent = if (total > 0) (known * 100 / total) else 0
            callback(percent)
        }
    }
}
