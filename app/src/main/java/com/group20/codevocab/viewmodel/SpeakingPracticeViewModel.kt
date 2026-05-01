package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.remote.dto.ModuleWithParentIdDto
import com.group20.codevocab.data.remote.dto.SpeakingAnalysisRequest
import com.group20.codevocab.data.remote.dto.SpeakingSentenceDto
import com.group20.codevocab.data.remote.dto.UserProfileResponse
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.data.repository.SpeakingPracticeRepository
import com.group20.codevocab.model.SpeakingAnalysisResult
import com.group20.codevocab.model.toResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

sealed class SpeakingPracticeState {
    data object Loading : SpeakingPracticeState()
    data class Success(val sentences: List<SpeakingSentenceDto>) : SpeakingPracticeState()
    data class Error(val message: String) : SpeakingPracticeState()
}

sealed class ModuleWithParentIdState {
    data object Loading : ModuleWithParentIdState()
    data class Success(val modules: List<ModuleWithParentIdDto>) : ModuleWithParentIdState()
    data class Error(val message: String) : ModuleWithParentIdState()
}

class SpeakingPracticeViewModel(
    private val repository: SpeakingPracticeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SpeakingPracticeState>(SpeakingPracticeState.Loading)
    val state: StateFlow<SpeakingPracticeState> = _state

    private val _analysisResult = MutableStateFlow<SpeakingAnalysisResult?>(null)
    val analysisResult: StateFlow<SpeakingAnalysisResult?> = _analysisResult

    private val _updatePhonemesResult = MutableStateFlow<Response<UserProfileResponse>?>(null)
    val updatePhonemesResult: StateFlow<Response<UserProfileResponse>?> = _updatePhonemesResult

    private val _moduleState = MutableStateFlow<ModuleWithParentIdState>(ModuleWithParentIdState.Loading)
    val moduleState: StateFlow<ModuleWithParentIdState> = _moduleState

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

    /**
     * Thực hiện phân tích câu nói của người dùng.
     * 
     * @param originalSentence Câu gốc mà người dùng cần đọc.
     * @param phoneticsTarget Phiên âm IPA mục tiêu.
     * @param recognizedSentence Câu mà hệ thống nhận diện được từ giọng nói.
     * @return SpeakingAnalysisResult kết quả phân tích (Model).
     */
    suspend fun analyzeSpeaking(
        originalSentence: String,
        phoneticsTarget: String,
        recognizedSentence: String
    ): SpeakingAnalysisResult? {
        return try {
            val request = SpeakingAnalysisRequest(
                originalSentence = originalSentence,
                phonetics = phoneticsTarget,
                spokenText = recognizedSentence
            )
            val response = repository.analyzeSpeaking(request)
            val result = response.toResult()
            _analysisResult.value = result
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Cập nhật danh sách các âm tiết yếu của người dùng lên server.
     * Trả về true nếu thành công, false nếu thất bại.
     */
    suspend fun updateWeakPhonemes(weakPhonemes: List<String>): Boolean {
        val userId = authRepository.getUserId() ?: return false
        return try {
            val response = repository.updateWeakPhonemes(userId, weakPhonemes)
            _updatePhonemesResult.value = response
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getModulesWithParentId() {
        viewModelScope.launch {
            _moduleState.value = ModuleWithParentIdState.Loading
            try {
                val modules = repository.getModulesWithParentId()
                _moduleState.value = ModuleWithParentIdState.Success(modules)
            } catch (e: Exception) {
                _moduleState.value = ModuleWithParentIdState.Error(e.message ?: "Failed to load modules")
            }
        }
    }
}
