package com.group20.codevocab.model

data class QuizQuestion(
    val vocabId: String, // Updated to String to match WordEntity ID
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val correctAnswerIndex: Int
)