package com.group20.codevocab.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val term: String,
    val definition: String
)
