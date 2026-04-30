package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.remote.dto.SpeakingSentenceDto
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.data.repository.SpeakingPracticeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SpeakingPracticeState {
    data object Loading : SpeakingPracticeState()
    data class Success(val sentences: List<SpeakingSentenceDto>) : SpeakingPracticeState()
    data class Error(val message: String) : SpeakingPracticeState()
}

class SpeakingPracticeViewModel(
    private val repository: SpeakingPracticeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SpeakingPracticeState>(SpeakingPracticeState.Loading)
    val state: StateFlow<SpeakingPracticeState> = _state

    fun loadSentences(moduleId: String) {
        val userId = authRepository.getUserId() ?: ""
        viewModelScope.launch {
            _state.value = SpeakingPracticeState.Loading
            try {
                val sentences = repository.getSpeakingSentences(userId, moduleId)
                _state.value = SpeakingPracticeState.Success(sentences)
            } catch (e: Exception) {
                _state.value = SpeakingPracticeState.Error(e.message ?: "Failed to load speaking sentences")
            }
        }
    }
}
