package com.group20.codevocab.data.repository

import com.group20.codevocab.data.local.dao.QuizResultDao
import com.group20.codevocab.data.local.dao.VocabDao
import com.group20.codevocab.data.local.entity.QuizResultEntity
import com.group20.codevocab.model.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(
    private val vocabDao: VocabDao,
    private val quizResultDao: QuizResultDao
) {

    /**
     * Tạo danh sách câu hỏi quiz cho module
     */
    suspend fun generateQuiz(moduleId: Int): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val vocabList = vocabDao.getVocabByModule(moduleId)

        // Nếu module quá ít từ → không quiz
        if (vocabList.size < 4) return@withContext emptyList<QuizQuestion>()

        return@withContext vocabList.map { vocab ->

            // Lấy 3 nghĩa sai
            val wrongChoices: MutableList<String> =
                vocabDao.getRandomMeaningsExcept(vocab.meaningVi ?: "", 3)
                    ?.toMutableList()
                    ?: mutableListOf()

            // Nếu dữ liệu ít => fallback nghĩa placeholder
            while (wrongChoices.size < 3) wrongChoices.add("Nghĩa sai ngẫu nhiên")

            // Trộn 1 nghĩa đúng + các nghĩa sai
            val options = wrongChoices.toMutableList().apply {
                add(vocab.meaningVi ?: "")
                shuffle()
            }

            QuizQuestion(
                vocabId = vocab.id,
                question = vocab.word ?: "",
                correctAnswer = vocab.meaningVi ?: "",
                options = options,
                correctAnswerIndex = 0
            )
        }
    }

    /**
     * Lưu kết quả quiz
     */
    suspend fun saveQuizResult(moduleId: Int, score: Int, total: Int) {
        withContext(Dispatchers.IO) {
            quizResultDao.insertResult(
                QuizResultEntity(
                    moduleId = moduleId,
                    score = score,
                    totalQuestions = total,
                    correctCount = 0
                )
            )
        }
    }

    /**
     * Lịch sử quiz
     */
    suspend fun getQuizHistory(moduleId: Int) =
        withContext(Dispatchers.IO) {
            quizResultDao.getResultsByModule(moduleId)
        }
}
