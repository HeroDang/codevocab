package com.group20.codevocab.data.remote

import com.group20.codevocab.data.remote.dto.SpeakingPracticeRequest
import com.group20.codevocab.data.remote.dto.SpeakingSentenceDto
import retrofit2.http.Body
import retrofit2.http.POST

interface SpeakingPracticeApiService {
    @POST("get_speaking_sentences")
    suspend fun getSpeakingSentences(
        @Body request: SpeakingPracticeRequest
    ): List<SpeakingSentenceDto>
}
