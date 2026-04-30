package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.SpeakingPracticeApiClient
import com.group20.codevocab.data.remote.dto.SpeakingPracticeRequest
import com.group20.codevocab.data.remote.dto.SpeakingSentenceDto
import kotlinx.coroutines.delay

class SpeakingPracticeRepository {
    private val api = SpeakingPracticeApiClient.api

    suspend fun getSpeakingSentences(userId: String, moduleId: String): List<SpeakingSentenceDto> {
        // Tạm thời fake data do server chưa hoạt động
        delay(1000) // Giả lập độ trễ mạng
        return listOf(
            SpeakingSentenceDto("How can I improve my coding skills?", "/haʊ kæn aɪ ɪmˈpruːv maɪ ˈkəʊdɪŋ skɪlz/"),
            SpeakingSentenceDto("I am learning Android development with Kotlin.", "/aɪ æm ˈlɜːrnɪŋ ˈændrɔɪd dɪˈveləpmənt wɪð ˈkɒtlɪn/"),
            SpeakingSentenceDto("Practice makes perfect.", "/ˈpræktɪs meɪks ˈpɜːrfɪkt/"),
            SpeakingSentenceDto("The weather is beautiful today.", "/ðə ˈweðər ɪz ˈbjuːtɪfəl təˈdeɪ/"),
            SpeakingSentenceDto("Could you please repeat that?", "/kʊd juː pliːz rɪˈpiːt ðæt/"),
            SpeakingSentenceDto("Kotlin is a modern programming language.", "/ˈkɒtlɪn ɪz ə ˈmɒdərn ˈprəʊɡræmɪŋ ˈlæŋɡwɪdʒ/"),
            SpeakingSentenceDto("Success requires hard work and dedication.", "/səkˈses rɪˈkwaɪərz hɑːrd wɜːrk ænd ˌdedɪˈkeɪʃən/"),
            SpeakingSentenceDto("What is your favorite part of coding?", "/wɒt ɪz jɔːr ˈfeɪvərɪt pɑːrt əv ˈkəʊdɪŋ/"),
            SpeakingSentenceDto("I enjoy solving complex problems.", "/aɪ ɪnˈdʒɔɪ ˈsɒlvɪŋ ˈkɒmpleks ˈprɒbləmz/"),
            SpeakingSentenceDto("Learning a new language is always exciting.", "/ˈlɜːrnɪŋ ə njuː ˈlæŋɡwɪdʒ ɪz ˈɔːlweɪz ɪkˈsaɪtɪŋ/")
        )
        
        // Code gốc gọi API khi server sẵn sàng:
        // val request = SpeakingPracticeRequest(userId, moduleId)
        // return api.getSpeakingSentences(request)
    }
}
