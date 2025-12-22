package com.group20.codevocab.data.repository

import com.group20.codevocab.model.QuizQuestion
import com.group20.codevocab.model.WordItem

/**
 * A repository that handles Quiz logic, like generating questions.
 * This class does not depend on any database or network source, it only processes data.
 */
class QuizRepository {

    /**
     * Creates a list of quiz questions from a list of words.
     * It needs at least 4 words to create meaningful questions (1 correct + 3 wrong options).
     *
     * @param words The list of WordItem objects fetched from the server.
     * @return A list of QuizQuestion objects, or an empty list if not enough words are provided.
     */
    fun createQuizQuestions(words: List<WordItem>): List<QuizQuestion> {
        if (words.size < 4) {
            return emptyList()
        }

        val allMeanings = words.map { it.meaningVi }

        return words.mapIndexed { index, word ->
            val correctAnswer = word.meaningVi

            // Get 3 other unique meanings from the list to act as wrong answers.
            val wrongAnswers = allMeanings.filter { it != correctAnswer }.shuffled().take(3)

            // Combine correct and wrong answers and shuffle them to create the final options.
            val options = (wrongAnswers + correctAnswer).shuffled()

            val correctAnswerIndex = options.indexOf(correctAnswer)

            QuizQuestion(
                vocabId = index, // Using list index as a placeholder ID
                question = word.textEn,
                options = options,
                correctAnswer = correctAnswer,
                correctAnswerIndex = correctAnswerIndex
            )
        }.shuffled() // Shuffle the final list of questions for a random order.
    }
}