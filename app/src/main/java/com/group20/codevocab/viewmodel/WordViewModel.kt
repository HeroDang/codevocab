package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.data.repository.VocabRepository
import com.group20.codevocab.data.repository.WordRepository
import com.group20.codevocab.model.WordItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WordListState {
    data object Loading : WordListState()
    data class Success(
        val title: String?,              // sub-module name (nếu bạn truyền qua)
        val items: List<WordItem>
    ) : WordListState()
    data class Error(val message: String) : WordListState()
}

class WordViewModel(private val repository: VocabRepository, private val repoWord: WordRepository) : ViewModel() {

    private val _words = MutableLiveData<List<WordEntity>>()
    val words: LiveData<List<WordEntity>> = _words

    private val _state = MutableStateFlow<WordListState>(WordListState.Loading)
    val state: StateFlow<WordListState> = _state

    fun loadWords(moduleId: String) {
        viewModelScope.launch {
            val wordList = repository.getVocabByModule(moduleId)
            _words.postValue(wordList)
        }
    }

    fun loadWordsFromServer(subModuleId: String, subModuleName: String? = null) {
        viewModelScope.launch {
            _state.value = WordListState.Loading
            runCatching {
                repoWord.getWordsRemote(subModuleId)
            }.onSuccess { items ->
                _state.value = WordListState.Success(subModuleName, items)
            }.onFailure { e ->
                _state.value = WordListState.Error(e.message ?: "Failed to load words")
            }
        }
    }
}