package com.group20.codevocab.data.remote


import com.google.gson.annotations.SerializedName
import com.group20.codevocab.data.remote.dto.ModuleDetailDto
import com.group20.codevocab.data.remote.dto.ModuleDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("words")
    suspend fun getWords(): List<WordsDto>

    @GET("modules")
    suspend fun getModules(): List<ModuleDto>

    @GET("modules/{moduleId}")
    suspend fun getModuleDetail(
        @Path("moduleId") moduleId: String
    ): ModuleDetailDto
}

data class WordsDto(
    @SerializedName("text_en")
    val textEn: String? = null,

    @SerializedName("meaning_vi")
    val meaningVi: String? = null,

    @SerializedName("part_of_speech")
    val partOfSpeech: String? = null,

    @SerializedName("ipa")
    val ipa: String? = null,

    @SerializedName("example_sentence")
    val exampleSentence: String? = null,

    @SerializedName("id")
    val id: String? = null
)
