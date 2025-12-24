package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.entity.WordEntity
import com.group20.codevocab.data.repository.VocabRepository
import com.group20.codevocab.data.repository.WordRepository
import com.group20.codevocab.model.WordItem
import com.group20.codevocab.model.toWordItem
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

    fun loadWords(moduleId: String, moduleName: String?) {
        viewModelScope.launch {
            _state.value = WordListState.Loading
            try {
                val wordList = repository.getVocabByModule(moduleId)
                val wordItems = wordList.map { it.toWordItem() }
                _state.value = WordListState.Success(moduleName, wordItems)
            } catch (e: Exception) {
                _state.value = WordListState.Error(e.message ?: "Failed to load local words")
            }
        }
    }

    fun saveWords(wordsToSave: List<WordEntity>) {
        viewModelScope.launch {
            repoWord.insertWords(wordsToSave)
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
    
    fun updateWordRemote(wordItem: WordItem, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            runCatching {
                repoWord.updateWordRemote(wordItem.id, wordItem)
            }.onSuccess {
                onSuccess()
            }.onFailure { e ->
                onError(e.message ?: "Failed to update word")
            }
        }
    }
}