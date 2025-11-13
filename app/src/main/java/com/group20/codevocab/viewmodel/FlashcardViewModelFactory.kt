package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.repository.FlashcardProgressRepository
import com.group20.codevocab.data.repository.VocabularyRepository

class FlashcardViewModelFactory(
    private val vocabRepo: VocabularyRepository,
    private val flashRepo: FlashcardProgressRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlashcardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlashcardViewModel(vocabRepo, flashRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
