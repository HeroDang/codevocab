package com.group20.codevocab.model

data class QuizQuestion(
    val vocabId: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)