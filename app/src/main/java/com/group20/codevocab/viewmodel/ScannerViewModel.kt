package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.repository.ScannerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class ScannerState {
    data object Idle : ScannerState()
    data object Loading : ScannerState()
    data class Success(val text: String) : ScannerState()
    data class Error(val message: String) : ScannerState()
}

class ScannerViewModel(private val repository: ScannerRepository) : ViewModel() {

    private val _state = MutableStateFlow<ScannerState>(ScannerState.Idle)
    val state: StateFlow<ScannerState> = _state

    fun scanImage(file: File) {
        // Prevent multiple scans while one is in progress
        if (_state.value is ScannerState.Loading) return

        viewModelScope.launch {
            _state.value = ScannerState.Loading
            try {
                val result = repository.scanImage(file)
                _state.value = ScannerState.Success(result)
            } catch (e: Exception) {
                _state.value = ScannerState.Error(e.message ?: "Failed to scan image")
            }
        }
    }

    /**
     * Resets the state to Idle, to be called after the UI has consumed an event (like navigation).
     */
    fun onResultConsumed() {
        _state.value = ScannerState.Idle
    }
}