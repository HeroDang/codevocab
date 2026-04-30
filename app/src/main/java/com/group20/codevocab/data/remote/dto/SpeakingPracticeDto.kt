package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SpeakingPracticeRequest(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("module_id")
    val moduleId: String
)

data class SpeakingSentenceDto(
    @SerializedName("text")
    val text: String,
    @SerializedName("phonetics")
    val phonetics: String
)
