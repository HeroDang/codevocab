package com.group20.codevocab.model

import com.google.gson.annotations.SerializedName

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
)