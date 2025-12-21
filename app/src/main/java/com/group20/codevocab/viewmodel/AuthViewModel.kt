package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.remote.dto.LoginResponse
import com.group20.codevocab.data.remote.dto.RegisterRequest
import com.group20.codevocab.data.remote.dto.RegisterResponse
import com.group20.codevocab.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Response<LoginResponse>>()
    val loginResult: LiveData<Response<LoginResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Response<RegisterResponse>>()
    val registerResult: LiveData<Response<RegisterResponse>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _errorMessage.value = "Username and password cannot be empty"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.login(username, password)
                _loginResult.value = response
                if (!response.isSuccessful) {
                    _errorMessage.value = "Login failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, name: String, password: String) {
        if (email.isBlank() || name.isBlank() || password.isBlank()) {
            _errorMessage.value = "Email, name, and password cannot be empty"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Khớp với Request Body trong tài liệu API của bạn
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    name = name,
                    avatarUrl = "" // Hoặc giá trị mặc định
                )
                val response = repository.register(request)
                _registerResult.value = response
                if (!response.isSuccessful) {
                    _errorMessage.value = "Registration failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
