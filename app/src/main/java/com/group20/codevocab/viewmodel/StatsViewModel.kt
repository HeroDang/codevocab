package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.group20.codevocab.data.repository.FlashcardProgressRepository

class StatsViewModel(private val repository: FlashcardProgressRepository) : ViewModel() {

    val totalWordsLearned = liveData {
        emit(repository.getTotalKnownWords())
    }

    val weeklyProgress = liveData {
        emit(repository.getWeeklyProgress())
    }
}
