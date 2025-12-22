package com.group20.codevocab.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DictionaryApiClient {

    private const val BASE_url = "https://api.dictionaryapi.dev/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: DictionaryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(DictionaryApiService::class.java)
    }
}
