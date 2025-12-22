package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.remote.DictionaryApiClient
import com.group20.codevocab.data.repository.DictionaryRepository

class AddWordViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddWordViewModel::class.java)) {
            val repo = DictionaryRepository(DictionaryApiClient.api)
            return AddWordViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
