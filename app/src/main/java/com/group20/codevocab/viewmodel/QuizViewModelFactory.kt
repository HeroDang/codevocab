package com.group20.codevocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.repository.QuizRepository

class QuizViewModelFactory(
    private val repository: QuizRepository,
    private val moduleId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(repository, moduleId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}