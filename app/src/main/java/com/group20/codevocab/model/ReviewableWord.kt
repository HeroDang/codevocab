package com.group20.codevocab.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewableWord(
    @SerializedName("text_en")
    val textEn: String?,
    @SerializedName("meaning_vi")
    val meaningVi: String?,
    val ipa: String?,
    @SerializedName("part_of_speech")
    val partOfSpeech: String?,
    @SerializedName("example_sentence")
    val exampleSentence: String?,
    var isChecked: Boolean = true
) : Parcelable

fun ReviewableWord.toWordItem(): WordItem {
    return WordItem(
        id = "", // We may not have an ID here, so we pass an empty string
        textEn = this.textEn ?: "",
        meaningVi = this.meaningVi ?: "",
        ipa = this.ipa,
        partOfSpeech = this.partOfSpeech,
        exampleSentence = this.exampleSentence,
        audioUrl = null
    )
}
