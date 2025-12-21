package com.group20.codevocab.data.remote


import com.google.gson.annotations.SerializedName
import com.group20.codevocab.data.remote.dto.ModuleDetailDto
import com.group20.codevocab.data.remote.dto.ModuleDto
import com.group20.codevocab.data.remote.dto.WordDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("words")
    suspend fun getWords(): List<WordsDto>

    @GET("modules")
    suspend fun getModules(): List<ModuleDto>

    @GET("modules/user_modules")
    suspend fun getUserModules(
        @Query("current_user_id") userId: String
    ): List<ModuleDto>

    @GET("modules/{moduleId}")
    suspend fun getModuleDetail(
        @Path("moduleId") moduleId: String
    ): ModuleDetailDto

    @GET("modules/{moduleId}/words")
    suspend fun getWordsBySubmodule(
        @Path("moduleId") subModuleId: String
    ): List<WordDto>

    @PUT("modules/{moduleId}")
    suspend fun updateModule(
        @Path("moduleId") moduleId: String,
        @Body module: ModuleDto
    ): ModuleDto
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
