package com.group20.codevocab.ui.quiz

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.group20.codevocab.R
import com.group20.codevocab.data.repository.QuizRepository
import com.group20.codevocab.databinding.ActivityQuizBinding
import com.group20.codevocab.model.QuizMistake
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
    private val mistakes = ArrayList<QuizMistake>()
    private var currentIndex = 0
    private var score = 0
    private var answerSubmitted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if this is a retry quiz
        val retryQuestions = getParcelableArrayList<QuizQuestion>(intent, "EXTRA_RETRY_QUESTIONS")

        if (retryQuestions != null && retryQuestions.isNotEmpty()) {
            // Start a retry quiz
            questions = retryQuestions
            quizAdapter = QuizAdapter(binding, this)
            setupUI()
            startQuiz()
        } else {
            // Start a normal quiz
            val moduleId = intent.getStringExtra("module_id")
            val moduleName = intent.getStringExtra("module_name")
            val isLocal = intent.getBooleanExtra("is_local", false)

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

            if (isLocal) {
                wordViewModel.loadWords(moduleId, moduleName)
            } else {
                wordViewModel.loadWordsFromServer(moduleId, moduleName)
            }
        }
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
                            Toast.makeText(this@QuizActivity, "Not enough words for a quiz.", Toast.LENGTH_LONG).show()
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
        mistakes.clear()
        showQuestion(questions[currentIndex])
    }

    private fun showQuestion(question: QuizQuestion) {
        answerSubmitted = false
        binding.tvQuestionCounter.text = "Question ${currentIndex + 1}/${questions.size}"
        binding.progressQuiz.max = questions.size
        binding.progressQuiz.progress = currentIndex + 1
        quizAdapter.bindQuestion(question)
        binding.btnSubmit.text = "Submit Answer"
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
            binding.btnSubmit.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green_500)
        } else {
            val yourAnswer = currentQuestion.options.getOrNull(selectedIndex) ?: ""
            mistakes.add(QuizMistake(currentQuestion, yourAnswer))
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
                putParcelableArrayListExtra("EXTRA_MISTAKES", mistakes)
            }
            startActivity(intent)
            finish()
        }
    }

    // Helper to get Parcelable ArrayList with SDK version check
    private inline fun <reified T : android.os.Parcelable> getParcelableArrayList(intent: Intent, key: String): ArrayList<T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(key)
        }
    }
}