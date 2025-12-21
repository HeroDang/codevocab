package com.group20.codevocab.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        TokenManager.accessToken?.let { token ->
            requestBuilder.addHeader(
                "Authorization",
                "Bearer $token"
            )
        }

        chain.proceed(requestBuilder.build())
    }

    // Standard client for fast APIs
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    // Client with long timeout for slow OCR API
    private val ocrOkHttp = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    // API Service for main backend
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // API Service for OCR backend
    val ocrApi: OcrApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.OCR_BASE_URL)
            .client(ocrOkHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OcrApiService::class.java)
    }
}