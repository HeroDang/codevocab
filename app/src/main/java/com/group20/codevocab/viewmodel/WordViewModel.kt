package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.repository.WordRepository
import com.group20.codevocab.data.local.entity.WordEntity
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {
    fun addWord(wordEntity: WordEntity) {
        viewModelScope.launch {
            repository.insertWord(wordEntity)
        }
    }
}
