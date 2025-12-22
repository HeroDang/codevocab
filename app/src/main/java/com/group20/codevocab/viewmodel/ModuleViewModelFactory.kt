package com.group20.codevocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.repository.ModuleRepository

class ModuleViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModuleViewModel::class.java)) {

            val db = AppDatabase.getDatabase(context)
            val moduleRepo = ModuleRepository(ApiClient.api ,db.moduleDao())

            return ModuleViewModel(moduleRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
