package com.group20.codevocab.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.group20.codevocab.R
import com.group20.codevocab.data.repository.QuizRepository
import com.group20.codevocab.databinding.ActivityQuizBinding
import com.group20.codevocab.model.QuizQuestion
import com.group20.codevocab.viewmodel.WordListState
import com.group20.codevocab.viewmodel.WordViewModel
import com.group20.codevocab.viewmodel.WordViewModelFactory
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var wordViewModel: WordViewModel
    private val quizRepository = QuizRepository()
    private lateinit var quizAdapter: QuizAdapter

    private var questions: List<QuizQuestion> = emptyList()
    private var currentIndex = 0
    private var score = 0
    private var answerSubmitted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val moduleId = intent.getStringExtra("module_id")
        if (moduleId == null) {
            Toast.makeText(this, "Module ID is missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val factory = WordViewModelFactory(applicationContext)
        wordViewModel = ViewModelProvider(this, factory)[WordViewModel::class.java]
        quizAdapter = QuizAdapter(binding, this)

        setupUI()
        observeViewModel()

        wordViewModel.loadWordsFromServer(moduleId, null) // Trigger data loading
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener {
            if (answerSubmitted) {
                goToNextQuestion()
            } else {
                submitAnswer()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            wordViewModel.state.collect { state ->
                when (state) {
                    is WordListState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.contentGroup.visibility = View.GONE
                    }
                    is WordListState.Success -> {
                        val words = state.items
                        questions = quizRepository.createQuizQuestions(words)

                        if (questions.isEmpty()) {
                            Toast.makeText(this@QuizActivity, "Not enough words to start a quiz (requires at least 4).", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            startQuiz()
                        }
                    }
                    is WordListState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@QuizActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun startQuiz() {
        binding.progressBar.visibility = View.GONE
        binding.contentGroup.visibility = View.VISIBLE
        currentIndex = 0
        score = 0
        showQuestion(questions[currentIndex])
    }

    private fun showQuestion(question: QuizQuestion) {
        answerSubmitted = false
        binding.tvQuestionCounter.text = "Question ${currentIndex + 1}/${questions.size}"
        binding.progressQuiz.max = questions.size
        binding.progressQuiz.progress = currentIndex + 1
        quizAdapter.bindQuestion(question)
        binding.btnSubmit.text = "Submit Answer"
        // Reset button color to the default
        binding.btnSubmit.backgroundTintList = ContextCompat.getColorStateList(this, R.color.blue_500)
    }

    private fun submitAnswer() {
        val selectedOptionId = binding.radioGroupOptions.checkedRadioButtonId
        if (selectedOptionId == -1) {
            Toast.makeText(this, "Please select an answer.", Toast.LENGTH_SHORT).show()
            return
        }

        answerSubmitted = true
        val currentQuestion = questions[currentIndex]

        val selectedIndex = when (selectedOptionId) {
            binding.option1.id -> 0
            binding.option2.id -> 1
            binding.option3.id -> 2
            binding.option4.id -> 3
            else -> -1
        }

        val isCorrect = selectedIndex == currentQuestion.correctAnswerIndex
        if (isCorrect) {
            score++
            // Set button color to green for correct answer
            binding.btnSubmit.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green_500)
        } else {
            // Set button color to red for incorrect answer
            binding.btnSubmit.backgroundTintList = ContextCompat.getColorStateList(this, R.color.status_red)
        }

        quizAdapter.showResult(currentQuestion.correctAnswerIndex, selectedIndex)
        binding.btnSubmit.text = "Next Question"
    }

    private fun goToNextQuestion() {
        currentIndex++
        if (currentIndex < questions.size) {
            showQuestion(questions[currentIndex])
        } else {
            val intent = Intent(this, QuizSummaryActivity::class.java).apply {
                putExtra("EXTRA_SCORE", score)
                putExtra("EXTRA_TOTAL_QUESTIONS", questions.size)
            }
            startActivity(intent)
            finish()
        }
    }
}