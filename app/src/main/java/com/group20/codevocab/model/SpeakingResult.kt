package com.group20.codevocab.model

data class SpeakingResult(
    val originalSentence: String,
    val phonetics: String,
    val recognizedSentence: String,
    val analysis: List<WordAnalysis>,
    val mispronouncedPhonemes: List<String>
)
