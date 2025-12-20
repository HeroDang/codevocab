package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DebugApiViewModel : ViewModel() {

    private val _text = MutableStateFlow("idle")
    val text: StateFlow<String> = _text

    fun ping() {
        viewModelScope.launch {
            try {
                val res = ApiClient.api.getWords()
                _text.value = "OK: ${res}"
            } catch (e: Exception) {
                _text.value = "ERR: ${e.message}"
            }
        }
    }
}
