package com.group20.codevocab.ui.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.repository.MarketRepository
import com.group20.codevocab.model.WordItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// State definition for the UI
sealed class WordListMarketState {
    object Loading : WordListMarketState()
    data class Success(val items: List<WordItem>, val title: String) : WordListMarketState()
    data class Error(val message: String) : WordListMarketState()
}

class WordListMarketViewModel : ViewModel() {

    private val marketRepository = MarketRepository()
    private val _state = MutableStateFlow<WordListMarketState>(WordListMarketState.Loading)
    val state: StateFlow<WordListMarketState> = _state

    fun loadWords(moduleId: String) {
        viewModelScope.launch {
            _state.value = WordListMarketState.Loading
            try {
                val words = marketRepository.getWordsOfModule(moduleId)
                // Assuming we can derive the title from somewhere, or we pass it along
                // For now, let's just use a generic title or pass it if available
                _state.value = WordListMarketState.Success(words, "Module Words")
            } catch (e: Exception) {
                _state.value = WordListMarketState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}