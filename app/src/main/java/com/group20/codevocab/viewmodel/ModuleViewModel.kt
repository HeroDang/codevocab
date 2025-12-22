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
    
    fun loadUserModulesFromServer(userId: String) {
        viewModelScope.launch {
            _state.value = ModulesState.Loading
            try {
                val items = repository.getUserModulesRemote(userId)
                _state.value = ModulesState.Success(items)
            } catch (e: Exception) {
                _state.value = ModulesState.Error(e.message ?: "Failed to load user modules")
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
                    loadUserModulesFromServer("9150dfe1-0758-4716-9d0e-99fc0fbe3a63")
                }
            } catch (e: Exception) {
                 _state.value = ModulesState.Error("Failed to update: ${e.message}")
            }
        }
    }
}