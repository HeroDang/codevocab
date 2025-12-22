package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)

data class RegisterResponse(
    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    @SerializedName("disabled")
    val disabled: Boolean,

    @SerializedName("role")
    val role: String,

    @SerializedName("id")
    val id: String
)