package com.group20.codevocab.model

data class WordItem(
    val id: String,
    val textEn: String,
    val meaningVi: String,
    val ipa: String?,
    val partOfSpeech: String?,
    val exampleSentence: String?,
    val audioUrl: String?
)
