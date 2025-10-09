package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.repository.ModuleRepository

class ModuleViewModelFactory(private val repository: ModuleRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModuleViewModel::class.java)) {
            return ModuleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
