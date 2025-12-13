package com.group20.codevocab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "word")
    val word: String,
    @ColumnInfo(name = "phonetic")
    val phonetic: String?,
    @ColumnInfo(name = "meaning_vi")
    val meaningVi: String?,
    @ColumnInfo(name = "example")
    val example: String?,
    @ColumnInfo(name = "part_of_speech")
    val partOfSpeech: String?,
    @ColumnInfo(name = "module_id")
    val moduleId: Int = 0
)
