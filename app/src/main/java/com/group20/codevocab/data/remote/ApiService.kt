package com.group20.codevocab.data.remote


import com.google.gson.annotations.SerializedName
import com.group20.codevocab.data.remote.dto.LoginResponse
import com.group20.codevocab.data.remote.dto.ModuleDetailDto
import com.group20.codevocab.data.remote.dto.ModuleDto
import com.group20.codevocab.data.remote.dto.RegisterRequest
import com.group20.codevocab.data.remote.dto.RegisterResponse
import com.group20.codevocab.data.remote.dto.StudySessionRequest
import com.group20.codevocab.data.remote.dto.StudySessionResponse
import com.group20.codevocab.data.remote.dto.UserDto
import com.group20.codevocab.data.remote.dto.WordDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserDto>

    // API Cập nhật thông tin profile
    @PATCH("auth/me")
    suspend fun updateProfile(
        @Body request: Map<String, String>
    ): Response<UserDto>

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

    @POST("study/sessions")
    suspend fun saveStudySession(
        @Body request: StudySessionRequest
    ): Response<StudySessionResponse>

    @GET("modules/")
    suspend fun getAllModules(): List<ModuleDto>

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
