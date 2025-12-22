package com.group20.codevocab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseViewModelFactory<T : ViewModel> :
    ViewModelProvider.Factory {

    abstract fun createViewModel(): T

    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        return createViewModel() as VM
    }
}
