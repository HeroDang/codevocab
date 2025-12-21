package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.QuizResultDao
import com.group20.codevocab.data.local.dao.WordDao
import com.group20.codevocab.data.local.entity.QuizResultEntity
import com.group20.codevocab.model.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(
    private val wordDao: WordDao,
    private val quizResultDao: QuizResultDao
) {

    /**
     * Tạo danh sách câu hỏi quiz cho module
     */
    suspend fun generateQuiz(moduleId: String): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val wordList = wordDao.getWordsByModule(moduleId)

        // Nếu module quá ít từ → không quiz
        if (wordList.size < 4) return@withContext emptyList<QuizQuestion>()

        return@withContext wordList.mapNotNull { word ->
            val correctMeaning = word.meaningVi ?: return@mapNotNull null

            // Lấy 3 nghĩa sai
            val wrongChoices: MutableList<String> =
                wordDao.getRandomMeaningsExcept(correctMeaning, 3)
                    .toMutableList()

            // Nếu dữ liệu ít => fallback nghĩa placeholder
            while (wrongChoices.size < 3) wrongChoices.add("Random Wrong Meaning")

            // Trộn 1 nghĩa đúng + các nghĩa sai
            val options = wrongChoices.toMutableList().apply {
                add(correctMeaning)
                shuffle()
            }

            QuizQuestion(
                vocabId = word.id, // Now String
                question = word.textEn,
                correctAnswer = correctMeaning,
                options = options,
                correctAnswerIndex = options.indexOf(correctMeaning)
            )
        }
    }

    /**
     * Lưu kết quả quiz
     */
    suspend fun saveQuizResult(moduleId: String, score: Int, total: Int) {
        withContext(Dispatchers.IO) {
            quizResultDao.insertResult(
                QuizResultEntity(
                    moduleId = moduleId,
                    score = score,
                    totalQuestions = total,
                    correctCount = score 
                )
            )
        }
    }

    /**
     * Lịch sử quiz
     */
    suspend fun getQuizHistory(moduleId: String) =
        withContext(Dispatchers.IO) {
            quizResultDao.getResultsByModule(moduleId)
        }
}