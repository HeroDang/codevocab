package com.group20.codevocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.repository.FlashcardProgressRepository

class StatsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            val db = AppDatabase.getDatabase(context)
            val repository = FlashcardProgressRepository(db.flashcardDao())
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
