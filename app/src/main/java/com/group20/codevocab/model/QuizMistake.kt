package com.group20.codevocab.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizMistake(
    val question: QuizQuestion,
    val yourAnswer: String
) : Parcelable