package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EmailCheckResponse(
    val exists: Boolean,
    val userId: String? // Assuming API returns userId if exists, otherwise null or not present
)

data class ShareModuleRequest(
    @SerializedName("to_user")
    val toUser: String,
    
    @SerializedName("module_id")
    val moduleId: String
)

data class ModuleCreateRequest(
    val name: String,
    val description: String?,
    @SerializedName("parent_id")
    val parentId: String? = null,
    @SerializedName("is_public")
    val isPublic: Boolean = false,
    @SerializedName("owner_id")
    val ownerId: String? = null
)

data class WordCreateRequest(
    @SerializedName("text_en")
    val textEn: String,
    @SerializedName("meaning_vi")
    val meaningVi: String?,
    @SerializedName("part_of_speech")
    val partOfSpeech: String?,
    val ipa: String?,
    @SerializedName("example_sentence")
    val exampleSentence: String?
)

data class WordListCreateRequest(
    @SerializedName("module_id")
    val moduleId: String,
    val words: List<WordCreateRequest>
)
