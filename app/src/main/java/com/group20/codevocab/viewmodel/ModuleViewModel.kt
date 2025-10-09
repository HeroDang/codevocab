package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.repository.ModuleRepository
import kotlinx.coroutines.launch

class ModuleViewModel(
    private val repository: ModuleRepository
) : ViewModel() {

    private val _modules = MutableLiveData<List<ModuleEntity>>()
    val modules: LiveData<List<ModuleEntity>> get() = _modules

    fun loadModules() {
        viewModelScope.launch {
            val data = repository.getAllModules()
            _modules.postValue(data)
        }
    }
}