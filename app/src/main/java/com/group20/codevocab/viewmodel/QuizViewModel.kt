package com.group20.codevocab.viewmodel

import androidx.lifecycle.*
import com.group20.codevocab.data.local.entity.QuizResultEntity
import com.group20.codevocab.data.repository.QuizRepository
import com.group20.codevocab.model.QuizQuestion
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: QuizRepository,
    private val moduleId: Int
) : ViewModel() {

    private var questions: List<QuizQuestion> = emptyList()
    private var currentIndex = 0

    private val _currentQuestion = MutableLiveData<QuizQuestion>()
    val currentQuestion: LiveData<QuizQuestion> get() = _currentQuestion

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> get() = _progress

    private val _quizFinished = MutableLiveData(false)
    val quizFinished: LiveData<Boolean> get() = _quizFinished

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            questions = repository.generateQuiz(moduleId)

            if (questions.isEmpty()) {
                _quizFinished.postValue(true)
                return@launch
            }

            _currentQuestion.postValue(questions[0])
            updateProgress()
        }
    }

    fun checkAnswer(optionIndex: Int): Boolean {
        val q = questions[currentIndex]
        val selected = q.options[optionIndex]
        val correct = q.correctAnswer

        val result = selected == correct

        if (result) {
            _score.value = (_score.value ?: 0) + 1
        }

        return result
    }

    fun nextQuestion() {
        if (currentIndex + 1 < questions.size) {
            currentIndex++
            _currentQuestion.postValue(questions[currentIndex])
            updateProgress()
        } else {
            finishQuiz()
        }
    }

    private fun updateProgress() {
        val p = (((currentIndex + 1).toFloat() / questions.size) * 100).toInt()
        _progress.postValue(p)
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            repository.saveQuizResult(
                moduleId = moduleId,
                score = score.value ?: 0,
                total = questions.size
            )
        }

        _quizFinished.postValue(true)
    }
}