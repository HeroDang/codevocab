package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShareResponse(
    @SerializedName("to_user") val toUser: String,
    @SerializedName("module_id") val moduleId: String,
    @SerializedName("id") val id: String,
    @SerializedName("from_user") val fromUser: String,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String
)
