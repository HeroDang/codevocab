package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StudySessionRequest(
    @SerializedName("module_id")
    val moduleId: String,
    
    @SerializedName("total_cards")
    val totalCards: Int,
    
    @SerializedName("know_count")
    val knowCount: Int,
    
    @SerializedName("hard_count")
    val hardCount: Int,
    
    @SerializedName("review_count")
    val reviewCount: Int,
    
    @SerializedName("accuracy")
    val accuracy: Int,
    
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
)

data class StudySessionResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("message")
    val message: String
)
