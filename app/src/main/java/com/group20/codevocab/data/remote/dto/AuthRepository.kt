package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.ApiService
import com.group20.codevocab.data.remote.dto.LoginResponse
import com.group20.codevocab.data.remote.dto.RegisterRequest
import com.group20.codevocab.data.remote.dto.RegisterResponse
import com.group20.codevocab.data.remote.dto.UserDto
import com.group20.codevocab.utils.UserSessionManager
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {
    private val sessionManager = UserSessionManager.getInstance()

    suspend fun login(username: String, password: String): Response<LoginResponse> {
        val response = apiService.login(username, password)
        return response
    }

    suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(request)
    }

    suspend fun getCurrentUser(): Response<UserDto> {
        val response = apiService.getCurrentUser()
        if (response.isSuccessful) {
            response.body()?.let { user ->
                sessionManager.saveUser(user)
            }
        }
        return response
    }

    // Single Source of Truth: Trả về user từ SessionManager (RAM/Prefs)
    fun getUser(): UserDto? {
        return sessionManager.getUser()
    }

    fun getUserId(): String? {
        return sessionManager.getUserId()
    }

    suspend fun updateProfile(name: String, email: String): Response<UserDto> {
        val body = mapOf(
            "name" to name,
            "email" to email
        )
        val response = apiService.updateProfile(body)
        if (response.isSuccessful) {
            response.body()?.let { user ->
                sessionManager.saveUser(user)
            }
        }
        return response
    }
}
