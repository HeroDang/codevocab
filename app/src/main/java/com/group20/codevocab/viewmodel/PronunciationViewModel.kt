package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.repository.PronunciationCheckRepository
import com.group20.codevocab.utils.AudioRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class PronunciationState {
    data object Idle : PronunciationState()
    data object Recording : PronunciationState()
    data object Processing : PronunciationState()
    data class Success(val score: Float, val recognized: String, val isCorrect: Boolean) : PronunciationState()
    data class Error(val message: String) : PronunciationState()
}

class PronunciationViewModel(
    private val repository: PronunciationCheckRepository,
    private val recorder: AudioRecorder
) : ViewModel() {

    private val _state = MutableStateFlow<PronunciationState>(PronunciationState.Idle)
    val state: StateFlow<PronunciationState> = _state

    private var recordedFile: File? = null

    fun startRecording(targetWord: String) {
        val file = recorder.startRecording()
        if (file != null) {
            recordedFile = file
            _state.value = PronunciationState.Recording
            
            // Tự động dừng ghi âm sau 5 giây
            viewModelScope.launch {
                delay(5000) // Chờ 5 giây
                
                // Kiểm tra xem trạng thái có đang là Recording không (tránh trường hợp user thoát hoặc lỗi)
                if (_state.value is PronunciationState.Recording) {
                    stopRecording(targetWord)
                }
            }
        } else {
            _state.value = PronunciationState.Error("Failed to start recording")
        }
    }

    // Hàm này được gọi nội bộ sau 5s
    private fun stopRecording(targetWord: String) {
        recorder.stopRecording()
        _state.value = PronunciationState.Processing

        viewModelScope.launch {
            try {
                if (recordedFile != null) {
                    val response = repository.checkPronunciation(recordedFile!!, targetWord)
                    _state.value = PronunciationState.Success(
                        score = response.score,
                        recognized = response.recognized,
                        isCorrect = response.is_correct
                    )
                } else {
                    _state.value = PronunciationState.Error("No recording file found")
                }
            } catch (e: Exception) {
                _state.value = PronunciationState.Error(e.message ?: "Failed to check pronunciation")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        recorder.release()
    }
}
