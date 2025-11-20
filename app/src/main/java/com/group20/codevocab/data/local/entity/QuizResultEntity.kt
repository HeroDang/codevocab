package com.group20.codevocab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "module_id")
    val moduleId: Int,
    @ColumnInfo(name = "score")
    val score: Int,
    @ColumnInfo(name = "correct_count")
    val correctCount: Int,
    @ColumnInfo(name = "total_questions")
    val totalQuestions: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
