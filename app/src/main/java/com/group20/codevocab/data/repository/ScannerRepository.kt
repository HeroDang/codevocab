package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.OcrApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ScannerRepository(private val api: OcrApiService) {
    suspend fun scanImage(file: File): String {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return api.ocrImage(body).string()
    }
}