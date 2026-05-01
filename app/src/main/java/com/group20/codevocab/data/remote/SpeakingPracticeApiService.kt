package com.group20.codevocab.data.remote

import com.group20.codevocab.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface SpeakingPracticeApiService {
    @POST("api/practice/generate")
    suspend fun getSpeakingSentences(
        @Body request: SpeakingPracticeRequest
    ): SpeakingPracticeResponse

    @POST("api/practice/evaluate")
    suspend fun analyzeSpeaking(
        @Body request: SpeakingAnalysisRequest
    ): SpeakingAnalysisResponse

    @PATCH("user-profiles/{user_id}/weak-phonemes")
    suspend fun updateWeakPhonemes(
        @Path("user_id") userId: String,
        @Body request: UserProfileUpdatePhonemes
    ): Response<UserProfileResponse>

    @GET("user-profiles/modules/with-parent-id")
    suspend fun getModulesWithParentId(): List<ModuleWithParentIdDto>
}
