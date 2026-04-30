package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.data.repository.SpeakingPracticeRepository

class SpeakingPracticeViewModelFactory(
    private val repository: SpeakingPracticeRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpeakingPracticeViewModel::class.java)) {
            return SpeakingPracticeViewModel(repository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
