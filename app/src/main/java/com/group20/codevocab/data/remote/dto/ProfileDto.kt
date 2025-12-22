package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProfileUpdateRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String
)