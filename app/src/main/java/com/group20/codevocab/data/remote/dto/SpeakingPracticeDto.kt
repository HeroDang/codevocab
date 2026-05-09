package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SpeakingPracticeRequest(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("module_id")
    val moduleId: String
)

data class SpeakingPracticeResponse(
    @SerializedName("extracted_vocab")
    val extractedVocab: List<String>,
    @SerializedName("level")
    val level: String,
    @SerializedName("practice_data")
    val practiceData: List<SpeakingSentenceDto>,
    @SerializedName("status")
    val status: String,
    @SerializedName("targeted_weak_phonemes")
    val targetedWeakPhonemes: List<String>
)

data class SpeakingSentenceDto(
    @SerializedName("sentence")
    val text: String,
    @SerializedName("ipa")
    val phonetics: String
)

data class SpeakingAnalysisRequest(
    @SerializedName("original_sentence")
    val originalSentence: String,
    @SerializedName("phonetics")
    val phonetics: String,
    @SerializedName("spoken_text")
    val spokenText: String
)

data class SpeakingAnalysisResponse(
    @SerializedName("original_sentence")
    val originalSentence: String,
    @SerializedName("recognized_sentence")
    val recognizedSentence: String,
    @SerializedName("analysis")
    val analysis: List<WordAnalysisDto>,
    @SerializedName("mispronounced_phonemes")
    val mispronouncedPhonemes: List<String>
)

data class WordAnalysisDto(
    @SerializedName("word")
    val word: String,
    @SerializedName("status")
    val status: String, // "correct" | "incorrect"
    @SerializedName("segments")
    val segments: List<SegmentDto>,
    //@SerializedName("phonetic_error")
    //val phoneticError: PhoneticErrorDto?
)

data class SegmentDto(
    @SerializedName("text")
    val text: String,
    @SerializedName("is_correct")
    val isCorrect: Boolean,
    @SerializedName("phonetic_error")
    val phoneticError: PhoneticErrorDto?
)

data class PhoneticErrorDto(
    @SerializedName("expected")
    val expected: String,
    @SerializedName("actual")
    val actual: String?,
    @SerializedName("note")
    val note: String
)

data class ModuleWithParentIdDto(
    val id: String,
    val name: String,
    val description: String,
    @SerializedName("module_type")
    val moduleType: String,
    @SerializedName("is_public")
    val isPublic: Boolean,
    @SerializedName("created_at")
    val createdAt: String
)
