package com.group20.codevocab.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SpeakingPracticeApiClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // API Service for AI backend (e.g. port 8001)
    val apiAi: SpeakingPracticeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.AI_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(SpeakingPracticeApiService::class.java)
    }

    // API Service for Main backend (e.g. port 8000)
    val apiPostgresql: SpeakingPracticeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(SpeakingPracticeApiService::class.java)
    }
}
