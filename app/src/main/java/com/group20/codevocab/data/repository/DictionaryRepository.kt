package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.ApiService
import com.group20.codevocab.data.remote.DictionaryApiService
import com.group20.codevocab.data.remote.dto.EmailCheckResponse
import com.group20.codevocab.data.remote.dto.ModuleCreateRequest
import com.group20.codevocab.data.remote.dto.ModuleDto
import com.group20.codevocab.data.remote.dto.ModuleShareOut
import com.group20.codevocab.data.remote.dto.ShareModuleRequest
import com.group20.codevocab.data.remote.dto.WordDto
import com.group20.codevocab.data.remote.dto.WordListCreateRequest
import com.group20.codevocab.data.remote.dto.WordCreateRequest

class DictionaryRepository(
    private val api: DictionaryApiService,
    private val mainApi: ApiService? = null // Optional to keep backward compatibility or use DI
) {
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
    
    // New methods for Share Module feature using main ApiService
    suspend fun checkEmail(email: String): EmailCheckResponse {
        return mainApi?.checkEmail(email)?.body() ?: EmailCheckResponse(exists = false, userId = null)
    }

    suspend fun shareModule(moduleId: String, userId: String): ModuleShareOut? {
        val request = ShareModuleRequest(toUser = userId, moduleId = moduleId)
        return mainApi?.shareModule(request)?.body()
    }

    suspend fun publishModule(moduleId: String): ModuleDto? {
        return mainApi?.publishModule(moduleId)?.body()
    }
    
    suspend fun createModule(name: String, description: String?, isPublic: Boolean, ownerId: String? = null): ModuleDto? {
        val request = ModuleCreateRequest(name = name, description = description, isPublic = isPublic, ownerId = ownerId)
        return mainApi?.createModule(request)
    }
    
    suspend fun createWordList(moduleId: String, words: List<WordDto>): List<WordDto>? {
        // Map WordDto to WordCreateRequest if necessary, or just use WordDto if API accepts it loosely.
        // Assuming API expects WordCreateRequest structure inside WordListCreateRequest
        val wordRequests = words.map {
            WordCreateRequest(
                textEn = it.textEn ?: "",
                meaningVi = it.meaningVi,
                partOfSpeech = it.partOfSpeech,
                ipa = it.ipa,
                exampleSentence = it.exampleSentence
            )
        }
        val request = WordListCreateRequest(moduleId = moduleId, words = wordRequests)
        return mainApi?.createWordList(request)
    }
}
