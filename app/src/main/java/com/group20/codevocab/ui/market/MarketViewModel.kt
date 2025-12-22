package com.group20.codevocab.ui.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.repository.MarketRepository
import com.group20.codevocab.model.ModuleItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Sealed class to represent the different states of the UI
sealed class MarketModulesState {
    object Loading : MarketModulesState()
    data class Success(val items: List<ModuleItem>) : MarketModulesState()
    data class Error(val message: String) : MarketModulesState()
}

class MarketViewModel : ViewModel() {

    private val marketRepository = MarketRepository()

    private val _state = MutableStateFlow<MarketModulesState>(MarketModulesState.Loading)
    val state: StateFlow<MarketModulesState> = _state

    fun loadMarketModules() {
        viewModelScope.launch {
            _state.value = MarketModulesState.Loading
            try {
                val modules = marketRepository.getMarketModules()
                _state.value = MarketModulesState.Success(modules)
            } catch (e: Exception) {
                _state.value = MarketModulesState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}