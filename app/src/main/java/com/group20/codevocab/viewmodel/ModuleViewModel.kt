package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.repository.ModuleRepository
import com.group20.codevocab.data.repository.ModuleProgressInfo
import com.group20.codevocab.model.ModuleDetailItem
import com.group20.codevocab.model.ModuleItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Collections.sort

enum class SortType {
    NAME, DATE, WORD_COUNT
}

sealed class ModulesState {
    data object Loading : ModulesState()
    data class Success(val items: List<ModuleItem>) : ModulesState()
    data class Error(val message: String) : ModulesState()
}

sealed class ModuleDetailState {
    data object Loading : ModuleDetailState()
    data class Success(val data: ModuleDetailItem) : ModuleDetailState()
    data class Error(val message: String) : ModuleDetailState()
}

class ModuleViewModel(
    private val repository: ModuleRepository
) : ViewModel() {

    private val _modules = MutableLiveData<List<ModuleEntity>>()
    val modules: LiveData<List<ModuleEntity>> get() = _modules

    private val _inProgressModules = MutableLiveData<List<Pair<ModuleEntity, ModuleProgressInfo>>>()
    val inProgressModules: LiveData<List<Pair<ModuleEntity, ModuleProgressInfo>>> get() = _inProgressModules

    private val _state = MutableStateFlow<ModulesState>(ModulesState.Loading)
    val state: StateFlow<ModulesState> = _state
    private val _moduleDetailState =
        MutableStateFlow<ModuleDetailState>(ModuleDetailState.Loading)
    val moduleDetailState: StateFlow<ModuleDetailState> = _moduleDetailState
    
    // Sort state separated for My Modules and Shared Modules
    private val _myModulesSortType = MutableLiveData<SortType>(SortType.NAME)
    val myModulesSortType: LiveData<SortType> = _myModulesSortType

    private val _sharedModulesSortType = MutableLiveData<SortType>(SortType.NAME)
    val sharedModulesSortType: LiveData<SortType> = _sharedModulesSortType

    fun setSortType(isShared: Boolean, type: SortType) {
        if (isShared) {
            _sharedModulesSortType.value = type
        } else {
            _myModulesSortType.value = type
        }
    }

    fun getSortType(isShared: Boolean): SortType {
        return if (isShared) {
            _sharedModulesSortType.value ?: SortType.NAME
        } else {
            _myModulesSortType.value ?: SortType.NAME
        }
    }

    // Helper suspend function to fetch data sequentially
    private suspend fun fetchLocalModules() {
        try {
            val data = repository.getAllModules()
            _modules.postValue(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun loadModules() {
        viewModelScope.launch {
            fetchLocalModules()
        }
    }

    suspend fun getWordCount(moduleId: String): Int {
        return repository.getWordCountForModule(moduleId)
    }

    fun loadInProgressModules() {
        viewModelScope.launch {
            try {
                val modules = repository.getInProgressModules()
                // ✅ Lọc ra những module chưa hoàn thành (số từ đã học < tổng số từ)
                val modulesWithProgress = modules.map { 
                    it to repository.getModuleProgressInfo(it.id)
                }.filter { (_, progress) ->
                    progress.processedCount < progress.totalCount
                }
                _inProgressModules.postValue(modulesWithProgress)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadGeneralModules() {
        viewModelScope.launch {
            val data = repository.getGeneralModules()
            _modules.postValue(data)
        }
    }

    fun getModuleById(id: String) = liveData {
        emit(repository.getModuleById(id))
    }
    
    fun loadModulesFromServer() {
        viewModelScope.launch {
            _state.value = ModulesState.Loading
            try {
                val items = repository.getModulesRemote()
                _state.value = ModulesState.Success(items)
            } catch (e: Exception) {
                _state.value = ModulesState.Error(e.message ?: "Failed to load modules")
            }
        }
    }
    
    fun loadMyModulesFromServer() {
        viewModelScope.launch {
            _state.value = ModulesState.Loading
            try {
                val items = repository.getMyModules()
                _state.value = ModulesState.Success(items)
            } catch (e: Exception) {
                _state.value = ModulesState.Error(e.message ?: "Failed to load user modules")
            }
        }
    }

    fun loadSharedWithMeModules() {
        viewModelScope.launch {
            _state.value = ModulesState.Loading
            try {
                val items = repository.getSharedWithMeModules()
                _state.value = ModulesState.Success(items)
            } catch (e: Exception) {
                _state.value = ModulesState.Error(e.message ?: "Failed to load shared modules")
            }
        }
    }

    fun loadModuleDetailFromServer(moduleId: String) {
        viewModelScope.launch {
            _moduleDetailState.value = ModuleDetailState.Loading
            try {
                val detail = repository.getModuleDetailRemote(moduleId)
                _moduleDetailState.value =
                    ModuleDetailState.Success(detail)
            } catch (e: Exception) {
                _moduleDetailState.value =
                    ModuleDetailState.Error(
                        e.message ?: "Failed to load module detail"
                    )
            }
        }
    }

    fun createModuleLocal(name: String) {
        viewModelScope.launch {
            repository.createModuleLocal(name)
            // Ensure we fetch AFTER creation in the same coroutine scope sequence
            fetchLocalModules()
        }
    }

    fun updateModule(item: ModuleItem) {
        viewModelScope.launch {
            try {
                repository.updateModule(item)
                
                if (item.isLocal) {
                    fetchLocalModules() // Refresh local sequentially
                } else {
                    // Refresh remote list
                    loadMyModulesFromServer()
                }
            } catch (e: Exception) {
                 _state.value = ModulesState.Error("Failed to update: ${e.message}")
            }
        }
    }

    suspend fun copyModuleToLocal(moduleItem: ModuleItem): String {
        return repository.copyModuleToLocal(moduleItem)
    }

    fun acceptShareModule(module: ModuleItem, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val success = repository.acceptShareModule(module.id)
                if (success) {
                    onSuccess()
                    loadSharedWithMeModules() // Reload list
                } else {
                    onError("Failed to accept module")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
}