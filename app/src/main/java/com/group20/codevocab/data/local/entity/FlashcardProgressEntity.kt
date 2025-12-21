package com.group20.codevocab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard_progress")
data class FlashcardProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "vocab_id")
    val vocabId: String,

    @ColumnInfo(name = "module_id")
    val moduleId: String, // Kept for quick access or redundancy, changed to String to match ModuleEntity

    @ColumnInfo(name = "is_known")
    val isKnown: Boolean = false,

    @ColumnInfo(name = "last_reviewed")
    val lastReviewed: Long? = null
)
