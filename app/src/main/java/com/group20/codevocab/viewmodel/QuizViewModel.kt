package com.group20.codevocab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group20.codevocab.data.repository.QuizRepository
import com.group20.codevocab.model.QuizQuestion
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: QuizRepository,
    private val moduleId: Int
) : ViewModel() {

    private val _questions = MutableLiveData<List<QuizQuestion>>()
    val questions: LiveData<List<QuizQuestion>> = _questions

    private val _currentQuestion = MutableLiveData<QuizQuestion>()
    val currentQuestion: LiveData<QuizQuestion> = _currentQuestion

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int> = _score

    private val _progress = MutableLiveData<Int>(0)
    val progress: LiveData<Int> = _progress

    private val _totalQuestions = MutableLiveData<Int>(0)
    val totalQuestions: LiveData<Int> = _totalQuestions

    private val _quizFinished = MutableLiveData<Boolean>(false)
    val quizFinished: LiveData<Boolean> = _quizFinished

    // Pair of <selectedIndex, isCorrect>
    private val _selectedAnswer = MutableLiveData<Pair<Int, Boolean>?>(null)
    val selectedAnswer: LiveData<Pair<Int, Boolean>?> = _selectedAnswer

    private var currentIndex = 0

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val loadedQuestions = repository.getQuizQuestions(moduleId)
            _questions.value = loadedQuestions
            _totalQuestions.value = loadedQuestions.size
            _currentQuestion.value = loadedQuestions.getOrNull(0)
            _progress.value = if (loadedQuestions.isEmpty()) 0 else 1
        }
    }

    fun checkAnswer(selectedIndex: Int): Boolean {
        val question = _currentQuestion.value ?: return false
        val isCorrect = question.correctAnswerIndex == selectedIndex

        if (isCorrect) {
            _score.value = (_score.value ?: 0) + 1
        }
        _selectedAnswer.value = Pair(selectedIndex, isCorrect)
        return isCorrect
    }

    fun nextQuestion() {
        _selectedAnswer.value = null // Reset highlight
        currentIndex++
        val questionList = _questions.value
        if (questionList != null && currentIndex < questionList.size) {
            _currentQuestion.value = questionList[currentIndex]
            _progress.value = currentIndex + 1
        } else {
            _quizFinished.value = true
        }
    }
}