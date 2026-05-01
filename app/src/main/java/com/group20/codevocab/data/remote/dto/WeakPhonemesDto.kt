package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserProfileUpdatePhonemes(
    @SerializedName("phonemes")
    val phonemes: List<String>
)

data class UserProfileResponse(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("weak_phonemes")
    val weakPhonemes: Map<String, Int>,
    @SerializedName("level")
    val level: String?
)
