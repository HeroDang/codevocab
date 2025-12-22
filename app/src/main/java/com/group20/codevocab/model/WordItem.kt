package com.group20.codevocab.model

import com.group20.codevocab.data.local.entity.WordEntity

data class WordItem(
    val id: String,
    val textEn: String,
    val meaningVi: String,
    val ipa: String?,
    val partOfSpeech: String?,
    val exampleSentence: String?,
    val audioUrl: String?
)

fun WordEntity.toWordItem(): WordItem {
    return WordItem(
        id = this.id,
        textEn = this.textEn,
        meaningVi = this.meaningVi ?: "", // Ensure non-null
        ipa = this.ipa,
        partOfSpeech = this.partOfSpeech,
        exampleSentence = this.exampleSentence,
        audioUrl = this.audioUrl
    )
}