package com.group20.codevocab.data.remote.dto

data class PronunciationResponse(
    val is_correct: Boolean,
    val recognized: String,
    val score: Float,
    val target: String
)
