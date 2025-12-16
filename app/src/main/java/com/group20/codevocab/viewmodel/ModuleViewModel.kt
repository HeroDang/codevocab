package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.repository.ModuleRepository
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
    private val _state = MutableStateFlow<ModulesState>(ModulesState.Loading)
    val state: StateFlow<ModulesState> = _state
    private val _moduleDetailState =
        MutableStateFlow<ModuleDetailState>(ModuleDetailState.Loading)
    val moduleDetailState: StateFlow<ModuleDetailState> = _moduleDetailState

    fun loadModules() {
        viewModelScope.launch {
            val data = repository.getAllModules()
            _modules.postValue(data)
        }
    }

    fun loadGeneralModules() {
        viewModelScope.launch {
            val data = repository.getGeneralModules()
            _modules.postValue(data)
        }
    }

    fun getModuleById(id: Int) = liveData {
        emit(repository.getModuleById(id))
    }

    fun getSubModules(parentId: Int) = liveData {
        emit(repository.getSubModules(parentId))
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

    fun loadModuleDetailFromServer(moduleId: String) {
        viewModelScope.launch {
            _moduleDetailState.value = ModuleDetailState.Loading
            try {
                val detail = repository.getModuleDetailRemote(moduleId)
                _moduleDetailState.value =
                    ModuleDetailState.Success(detail)
            } catch (e: Exception) {

                // 🔁 fallback local – GIỮ CODE CŨ (nếu có)
                /*
                val local = repository.getModuleDetailLocal(moduleId)
                _moduleDetailState.value =
                    ModuleDetailState.Success(local)
                */

                _moduleDetailState.value =
                    ModuleDetailState.Error(
                        e.message ?: "Failed to load module detail"
                    )
            }
        }
    }
}