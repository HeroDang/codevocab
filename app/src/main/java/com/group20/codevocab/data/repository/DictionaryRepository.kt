package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.DictionaryApiService

class DictionaryRepository(private val api: DictionaryApiService) {
    suspend fun getPhonetic(word: String): String? {
        return try {
            val response = api.getPhonetic(word)
            if (response.isNotEmpty()) {
                // Find the first non-null phonetic
                val entry = response[0]
                entry.phonetic
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
