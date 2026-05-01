package com.group20.codevocab.data.repository

import com.group20.codevocab.data.remote.SpeakingPracticeApiClient
import com.group20.codevocab.data.remote.dto.*
import kotlinx.coroutines.delay
import retrofit2.Response

class SpeakingPracticeRepository {
    private val apiAi = SpeakingPracticeApiClient.apiAi
    private val apiPostgresql = SpeakingPracticeApiClient.apiPostgresql

    suspend fun getSpeakingSentences(userId: String, moduleId: String): List<SpeakingSentenceDto> {
        // Tạm thời fake data do server chưa hoạt động
        delay(1000) // Giả lập độ trễ mạng
        return listOf(
            SpeakingSentenceDto("How can I improve my coding skills?", "/haʊ kæn aɪ ɪmˈpruːv maɪ ˈkəʊdɪŋ skɪlz/"),
            SpeakingSentenceDto("I am learning Android development with Kotlin.", "/aɪ æm ˈlɜːrnɪŋ ˈændrɔɪd dɪˈveləpmənt wɪð ˈkɒtlɪn/"),
            SpeakingSentenceDto("Practice makes perfect.", "/ˈpræktɪs meɪks ˈpɜːrfɪkt/"),
            //SpeakingSentenceDto("The weather is beautiful today.", "/ðə ˈweðər ɪz ˈbjuːtɪfəl təˈdeɪ/"),
            //SpeakingSentenceDto("Could you please repeat that?", "/kʊd juː pliːz rɪˈpiːt ðæt/"),
            //SpeakingSentenceDto("Kotlin is a modern programming language.", "/ˈkɒtlɪn ɪz ə ˈmɒdərn ˈprəʊɡræmɪŋ ˈlæŋɡwɪdʒ/"),
            //SpeakingSentenceDto("Success requires hard work and dedication.", "/səkˈses rɪˈkwaɪərz hɑːrd wɜːrk ænd ˌdedɪˈkeɪʃən/"),
            //SpeakingSentenceDto("What is your favorite part of coding?", "/wɒt ɪz jɔːr ˈfeɪvərɪt pɑːrt əv ˈkəʊdɪŋ/"),
            //SpeakingSentenceDto("I enjoy solving complex problems.", "/aɪ ɪnˈdʒɔɪ ˈsɒlvɪŋ ˈkɒmpleks ˈprɒbləmz/"),
            //SpeakingSentenceDto("Learning a new language is always exciting.", "/ˈlɜːrnɪŋ ə njuː ˈlæŋɡwɪdʒ ɪz ˈɔːlweɪz ɪkˈsaɪtɪŋ/")
        )
        
        // Code gốc gọi API khi server sẵn sàng:
        /*return try {
            val request = SpeakingPracticeRequest(userId, moduleId)
            val response = apiAi.getSpeakingSentences(request)
            if (response.status == "success") {
                response.practiceData
            } else {
                throw Exception("API Error: ${response.status}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }*/
    }

    suspend fun analyzeSpeaking(request: SpeakingAnalysisRequest): SpeakingAnalysisResponse {
        // Tạm thời fake data do server chưa hoạt động
        /*delay(1000)
        
        val originalWords = request.originalSentence.trim().split(Regex("\\s+"))
        val recognizedWords = request.spokenText.trim().split(Regex("\\s+"))
        
        // Tách IPA target thành từng phần để fake dữ liệu phonetic_error
        val ipaParts = request.phonetics.trim()
            .removeSurrounding("/")
            .split(Regex("\\s+"))
        
        val wordAnalysisList = originalWords.mapIndexed { index, word ->
            val recognizedWord = recognizedWords.getOrNull(index) ?: ""
            // So sánh cơ bản để fake kết quả
            val cleanOriginal = word.lowercase().replace(Regex("[^a-z]"), "")
            val cleanRecognized = recognizedWord.lowercase().replace(Regex("[^a-z]"), "")
            
            val isCorrect = cleanOriginal == cleanRecognized && cleanOriginal.isNotEmpty()
            
            WordAnalysisDto(
                word = word,
                status = if (isCorrect) "correct" else "incorrect",
                segments = listOf(
                    SegmentDto(word, isCorrect)
                ),
                phoneticError = if (!isCorrect) {
                    PhoneticErrorDto(
                        expected = if (index < ipaParts.size) "/${ipaParts[index]}/" else "/.../",
                        actual = if (recognizedWord.isNotEmpty()) "/${recognizedWord.lowercase()}/" else null,
                        note = if (recognizedWord.isEmpty()) "Missing word" else "Mispronounced"
                    )
                } else null
            )
        }

        // Giả lập danh sách các âm tiết phát âm sai nếu có lỗi
        val mispronouncedPhonemes = if (wordAnalysisList.any { it.status == "incorrect" }) {
            listOf("θ", "r", "æ")
        } else {
            emptyList()
        }

        return SpeakingAnalysisResponse(
            originalSentence = request.originalSentence,
            recognizedSentence = request.spokenText,
            analysis = wordAnalysisList,
            mispronouncedPhonemes = mispronouncedPhonemes
        )*/

        // Code gốc gọi API khi server sẵn sàng:
        return try {
            apiAi.analyzeSpeaking(request)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun updateWeakPhonemes(
        userId: String,
        weakPhonemes: List<String>
    ): Response<UserProfileResponse> {
        return try {
            val request = UserProfileUpdatePhonemes(weakPhonemes)
            apiPostgresql.updateWeakPhonemes(userId, request)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
