package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String
)

data class UserDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name") // Khớp với API của bạn
    val name: String,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    @SerializedName("role")
    val role: String?
)