package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.repository.WordRepository
import com.group20.codevocab.model.Word
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {
    fun addWord(word: Word) {
        viewModelScope.launch {
            repository.insertWord(word)
        }
    }
}
