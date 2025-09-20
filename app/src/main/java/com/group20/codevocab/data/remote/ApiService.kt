package com.group20.codevocab.data.remote

import retrofit2.http.GET

interface ApiService {
    @GET("words")
    suspend fun getWords(): List<String>
}
