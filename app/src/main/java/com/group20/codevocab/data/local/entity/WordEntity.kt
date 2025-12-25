package com.group20.codevocab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "module_id")
    val moduleId: String,

    @ColumnInfo(name = "text_en")
    val textEn: String,

    @ColumnInfo(name = "meaning_vi")
    val meaningVi: String?,

    @ColumnInfo(name = "part_of_speech")
    val partOfSpeech: String?,

    @ColumnInfo(name = "ipa")
    val ipa: String?,

    @ColumnInfo(name = "example_sentence")
    val exampleSentence: String?,

    @ColumnInfo(name = "audio_url")
    val audioUrl: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: String?,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false
)
