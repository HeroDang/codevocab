package com.group20.codevocab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val term: String,
    val definition: String
)
