package com.group20.codevocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.repository.FlashcardProgressRepository
import com.group20.codevocab.data.repository.VocabularyRepository

class FlashcardViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlashcardViewModel::class.java)) {
            val db = AppDatabase.getDatabase(context)
            val vocabRepo = VocabularyRepository(db.wordDao())
            val flashRepo = FlashcardProgressRepository(db.flashcardDao())
            @Suppress("UNCHECKED_CAST")
            return FlashcardViewModel(vocabRepo, flashRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}