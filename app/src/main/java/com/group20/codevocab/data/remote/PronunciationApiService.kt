package com.group20.codevocab.data.remote

import com.group20.codevocab.data.remote.dto.PronunciationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PronunciationApiService {
    @Multipart
    @POST("check_pronunciation")
    suspend fun checkPronunciation(
        @Part file: MultipartBody.Part,
        @Part("target") target: RequestBody
    ): PronunciationResponse
}
