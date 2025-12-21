package com.group20.codevocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.repository.ModuleRepository
import com.group20.codevocab.data.repository.VocabRepository
import com.group20.codevocab.data.repository.WordRepository

class WordViewModelFactory(private val context: Context) : BaseViewModelFactory<WordViewModel>() {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
//            val db = AppDatabase.getDatabase(context)
//            val repoWord = WordRepository(ApiClient.api ,db.wordDao())
//            val repoVocab = VocabRepository(db.vocabDao())
//
//            @Suppress("UNCHECKED_CAST")
//            return WordViewModel(repoVocab, repoWord) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
    override fun createViewModel(): WordViewModel {
        val db = AppDatabase.getDatabase(context)

        val repoWord = WordRepository(
            ApiClient.api,
            db.wordDao()
        )

        val repoVocab = VocabRepository(
            db.vocabDao()
        )

        return WordViewModel(repoVocab, repoWord)
    }
}