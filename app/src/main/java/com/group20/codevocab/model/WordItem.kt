package com.group20.codevocab.model

import android.os.Parcelable
import com.group20.codevocab.data.local.entity.WordEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class WordItem(
    val id: String,
    val textEn: String,
    val meaningVi: String,
    val ipa: String?,
    val partOfSpeech: String?,
    val exampleSentence: String?,
    val audioUrl: String?
) : Parcelable

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

fun WordItem.toEntity(moduleId: String): WordEntity {
    return WordEntity(
        id = this.id,
        moduleId = moduleId,
        textEn = this.textEn,
        meaningVi = this.meaningVi,
        partOfSpeech = this.partOfSpeech,
        ipa = this.ipa,
        exampleSentence = this.exampleSentence,
        audioUrl = this.audioUrl,
        createdAt = null
    )
}
