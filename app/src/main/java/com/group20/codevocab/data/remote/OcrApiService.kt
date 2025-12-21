package com.group20.codevocab.data.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrApiService {
    @Multipart
    @POST("ocr") // The path is simple since Base URL is set in Retrofit instance
    suspend fun ocrImage(
        @Part file: MultipartBody.Part
    ): ResponseBody
}