package com.group20.codevocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.local.TokenManager
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.remote.DictionaryApiClient
import com.group20.codevocab.data.repository.DictionaryRepository
import com.group20.codevocab.data.repository.ModuleRepository
import com.group20.codevocab.data.repository.WordRepository

class DictionaryViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DictionaryViewModel::class.java)) {
            val api = DictionaryApiClient.api
            val db = AppDatabase.getDatabase(context)
            
            val mainApi = ApiClient.api
            val tokenManager = TokenManager(context)
            
            val repository = DictionaryRepository(api, mainApi)
            val moduleRepo = ModuleRepository(mainApi, db.moduleDao(), db.flashcardDao(), db.wordDao())
            val wordRepository = WordRepository(mainApi, db.wordDao())
            
            @Suppress("UNCHECKED_CAST")
            return DictionaryViewModel(repository, moduleRepo, wordRepository, tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
