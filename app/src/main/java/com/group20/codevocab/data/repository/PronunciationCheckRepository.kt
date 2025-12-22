package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.PronunciationApiService
import com.group20.codevocab.data.remote.dto.PronunciationResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class PronunciationCheckRepository(private val api: PronunciationApiService) {

    suspend fun checkPronunciation(file: File, targetWord: String): PronunciationResponse {
        val requestFile = file.asRequestBody("audio/mp3".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val target = targetWord.toRequestBody("text/plain".toMediaTypeOrNull())

        return api.checkPronunciation(body, target)
    }
}
