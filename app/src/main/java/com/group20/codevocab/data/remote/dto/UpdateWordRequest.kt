package com.group20.codevocab.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateWordRequest(
    @SerializedName("text_en")
    val textEn: String,
    
    @SerializedName("meaning_vi")
    val meaningVi: String,
    
    @SerializedName("part_of_speech")
    val partOfSpeech: String?,
    
    @SerializedName("ipa")
    val ipa: String?,
    
    @SerializedName("example_sentence")
    val exampleSentence: String?
)
