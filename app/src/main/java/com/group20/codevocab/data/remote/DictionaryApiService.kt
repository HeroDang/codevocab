package com.group20.codevocab.data.remote

import com.group20.codevocab.data.remote.dto.DictionaryResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    suspend fun getPhonetic(@Path("word") word: String): List<DictionaryResponse>
}
