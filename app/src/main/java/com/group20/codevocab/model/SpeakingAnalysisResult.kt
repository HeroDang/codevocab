package com.group20.codevocab.model

data class SpeakingAnalysisResult(
    val originalSentence: String,
    val recognizedSentence: String,
    val analysis: List<WordAnalysis>,
    val mispronouncedPhonemes: List<String>
)

data class WordAnalysis(
    val word: String,
    val status: String, // "correct" | "incorrect"
    val segments: List<SpeakingSegment>,
    //val phoneticError: PhoneticError?
)

data class SpeakingSegment(
    val text: String,
    val isCorrect: Boolean,
    val phoneticError: PhoneticError?
)

data class PhoneticError(
    val expected: String,
    val actual: String?,
    val note: String
)
