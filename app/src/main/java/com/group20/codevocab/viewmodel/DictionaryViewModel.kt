package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.TokenManager
import com.group20.codevocab.data.repository.DictionaryRepository
import com.group20.codevocab.data.repository.ModuleRepository
import com.group20.codevocab.data.repository.WordRepository
import com.group20.codevocab.model.toDto
import com.group20.codevocab.model.toWordDto
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val repository: DictionaryRepository,
    private val moduleRepository: ModuleRepository? = null,
    private val wordRepository: WordRepository? = null,
    private val tokenManager: TokenManager? = null
) : ViewModel() {

    private val _phonetic = MutableLiveData<String?>()
    val phonetic: LiveData<String?> = _phonetic
    
    private val _shareResult = MutableLiveData<ShareResult?>()
    val shareResult: LiveData<ShareResult?> = _shareResult
    
    private val _emailCheckResult = MutableLiveData<Boolean?>()
    val emailCheckResult: LiveData<Boolean?> = _emailCheckResult

    fun getPhonetic(word: String) {
        viewModelScope.launch {
            val result = repository.getPhonetic(word)
            _phonetic.postValue(result)
        }
    }
    
    fun shareModule(moduleId: String, email: String?, isPublish: Boolean, isLocal: Boolean) {
        viewModelScope.launch {
            try {
                var finalModuleId = moduleId
                
                // --- Logic for Local Module ---
                if (isLocal) { 
                    // Need to fetch module details to create it on server
                    val module = moduleRepository?.getModuleById(moduleId)
                    
                    if (module != null) {
                        // Create module on server
                        val userId = tokenManager?.getUserId()
                        val createdModule = repository.createModule(module.name, module.description, isPublish, userId)
                        if (createdModule != null) {
                            finalModuleId = createdModule.id ?: ""
                            
                            // Create words on server
                            val words = wordRepository?.getVocabByModule(moduleId) 
                            if (!words.isNullOrEmpty()) {
                                val wordDtos = words.map { it.toWordDto() }
                                repository.createWordList(finalModuleId, wordDtos)
                            }
                            
                            // Delete local logic 
                            moduleRepository?.deleteModuleLocal(moduleId)
                        } else {
                            _shareResult.postValue(ShareResult.Error("Failed to create module on server"))
                            return@launch
                        }
                    } else {
                        _shareResult.postValue(ShareResult.Error("Local module not found"))
                        return@launch
                    }
                }
                
                // --- Logic for Publish ---
                if (isPublish) {
                    repository.publishModule(finalModuleId)
                }
                
                // --- Logic for Email Share ---
                if (!email.isNullOrBlank()) {
                    val checkResponse = repository.checkEmail(email)
                    if (checkResponse.exists && checkResponse.userId != null) {
                         val shareRes = repository.shareModule(finalModuleId, checkResponse.userId)
                         if (shareRes != null) {
                             _shareResult.postValue(ShareResult.Success("Shared successfully"))
                         } else {
                             _shareResult.postValue(ShareResult.Error("Failed to share module"))
                         }
                    } else {
                        _shareResult.postValue(ShareResult.UserNotFound)
                    }
                } else if (isPublish) {
                    _shareResult.postValue(ShareResult.Success("Module published to market"))
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                _shareResult.postValue(ShareResult.Error(e.message ?: "Unknown error"))
            }
        }
    }
    
    fun resetShareResult() {
        _shareResult.value = null
    }
}

sealed class ShareResult {
    data class Success(val message: String) : ShareResult()
    data class Error(val message: String) : ShareResult()
    object UserNotFound : ShareResult()
}
