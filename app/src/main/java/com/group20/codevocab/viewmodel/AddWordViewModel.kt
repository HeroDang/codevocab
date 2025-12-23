package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.repository.DictionaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class IpaState {
    data object Idle : IpaState()
    data object Loading : IpaState()
    data class Success(val phonetic: String) : IpaState()
    data class Error(val message: String) : IpaState()
}

class AddWordViewModel(private val repository: DictionaryRepository) : ViewModel() {

    private val _ipaState = MutableStateFlow<IpaState>(IpaState.Idle)
    val ipaState: StateFlow<IpaState> = _ipaState

    fun getPhonetic(word: String) {
        viewModelScope.launch {
            _ipaState.value = IpaState.Loading
            val phonetic = repository.getPhonetic(word)
            if (phonetic != null) {
                _ipaState.value = IpaState.Success(phonetic)
            } else {
                _ipaState.value = IpaState.Error("Not Found")
            }
        }
    }
}
