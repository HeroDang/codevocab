package com.group20.codevocab.data.remote.dto
import com.google.gson.annotations.SerializedName

data class WordDto(
    val id: String,
    @SerializedName("text_en") val textEn: String?,
    @SerializedName("meaning_vi") val meaningVi: String?,
    @SerializedName("part_of_speech") val partOfSpeech: String?,
    val ipa: String?,
    @SerializedName("example_sentence") val exampleSentence: String?,
    @SerializedName("audio_url") val audioUrl: String?
)