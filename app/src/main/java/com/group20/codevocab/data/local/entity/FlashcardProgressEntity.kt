package com.group20.codevocab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard_progress")
data class FlashcardProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "vocab_id")
    val vocabId: Int,

    @ColumnInfo(name = "module_id")
    val moduleId: Int = 0,

    @ColumnInfo(name = "is_known")
    val isKnown: Boolean = false,

    @ColumnInfo(name = "last_reviewed")
    val lastReviewed: Long? = null
)
