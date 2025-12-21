package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.ApiService
import com.group20.codevocab.data.remote.dto.LoginResponse
import com.group20.codevocab.data.remote.dto.RegisterRequest
import com.group20.codevocab.data.remote.dto.RegisterResponse
import com.group20.codevocab.data.remote.dto.UserDto
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {
    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return apiService.login(username, password)
    }

    suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(request)
    }

    suspend fun getCurrentUser(): Response<UserDto> {
        return apiService.getCurrentUser()
    }

    suspend fun updateProfile(name: String, email: String): Response<UserDto> {
        val body = mapOf(
            "name" to name,
            "email" to email
        )
        return apiService.updateProfile(body)
    }
}
