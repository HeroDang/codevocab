package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.entity.VocabularyEntity
import com.group20.codevocab.data.repository.VocabRepository
import kotlinx.coroutines.launch

class WordViewModel(private val repository: VocabRepository) : ViewModel() {

    private val _words = MutableLiveData<List<VocabularyEntity>>()
    val words: LiveData<List<VocabularyEntity>> = _words

    fun loadWords(moduleId: Int) {
        viewModelScope.launch {
            val vocabList = repository.getVocabByModule(moduleId)
            _words.postValue(vocabList)
        }
    }
}