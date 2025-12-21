package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.remote.dto.UserDto
import com.group20.codevocab.data.repository.AuthRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _userData = MutableLiveData<UserDto?>()
    val userData: LiveData<UserDto?> = _userData

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchCurrentUser() {
        viewModelScope.launch {
            try {
                val response = repository.getCurrentUser()
                if (response.isSuccessful) {
                    _userData.value = response.body()
                } else {
                    _errorMessage.value = "Failed to load user data"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
            }
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            try {
                val response = repository.updateProfile(name, email)
                if (response.isSuccessful) {
                    _userData.value = response.body()
                    _updateStatus.value = true
                } else {
                    _errorMessage.value = "Failed to update profile"
                    _updateStatus.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Update error: ${e.localizedMessage}"
                _updateStatus.value = false
            }
        }
    }
}
