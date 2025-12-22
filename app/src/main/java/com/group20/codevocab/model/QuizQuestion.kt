package com.group20.codevocab.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizQuestion(
    val vocabId: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val correctAnswerIndex: Int
) : Parcelable