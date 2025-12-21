package com.group20.codevocab.ui.quiz

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.group20.codevocab.R
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.repository.QuizRepository
import com.group20.codevocab.databinding.ActivityQuizBinding
import com.group20.codevocab.viewmodel.QuizViewModel
import com.group20.codevocab.viewmodel.QuizViewModelFactory

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var viewModel: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val moduleId = intent.getStringExtra("module_id")
        if (moduleId.isNullOrEmpty()) {
            Toast.makeText(this, "ModuleId missing!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViewModel(moduleId)
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel(moduleId: String) {
        val db = AppDatabase.getDatabase(this)
        val repo = QuizRepository(db.wordDao(), db.quizResultDao())

        val factory = QuizViewModelFactory(repo, moduleId)
        viewModel = factory.create(QuizViewModel::class.java)
    }

    private fun setupObservers() {
        // Cập nhật UI mỗi khi hiện câu hỏi mới
        viewModel.currentQuestion.observe(this) { q ->
            binding.tvQuestion.text = q.question

//            binding.tvOption1.text = q.options[0]
//            binding.tvOption2.text = q.options[1]
//            binding.tvOption3.text = q.options[2]
//            binding.tvOption4.text = q.options[3]

            resetOptionUI()
        }

        // Điểm
        viewModel.score.observe(this) {
//            binding.tvScore.text = "Score: $it"
        }

        // Tiến trình
        viewModel.progress.observe(this) { p ->
            binding.progressQuiz.progress = p
        }

        // Quiz kết thúc
        viewModel.quizFinished.observe(this) { finished ->
            if (finished) {
                Toast.makeText(this, "Quiz completed!", Toast.LENGTH_SHORT).show()

                // TODO: Mở SummaryActivity
                finish()
            }
        }
    }

    private fun setupListeners() {
//        binding.cardOption1.setOnClickListener { selectAnswer(0) }
//        binding.cardOption2.setOnClickListener { selectAnswer(1) }
//        binding.cardOption3.setOnClickListener { selectAnswer(2) }
//        binding.cardOption4.setOnClickListener { selectAnswer(3) }
//
//        binding.btnNext.setOnClickListener {
//            viewModel.nextQuestion()
//        }
    }

    private fun selectAnswer(index: Int) {
        val isCorrect = viewModel.checkAnswer(index)

        highlightAnswer(index, isCorrect)
    }

    private fun highlightAnswer(index: Int, isCorrect: Boolean) {
        val correctColor = ContextCompat.getColor(this, R.color.blue_600)
        val wrongColor = ContextCompat.getColor(this, R.color.colorError)

//        val card = when (index) {
//            0 -> binding.cardOption1
//            1 -> binding.cardOption2
//            2 -> binding.cardOption3
//            else -> binding.cardOption4
//        }

//        card.setCardBackgroundColor(if (isCorrect) correctColor else wrongColor)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun resetOptionUI() {
        val defaultColor = ContextCompat.getColor(this, R.color.white)

//        binding.cardOption1.setCardBackgroundColor(defaultColor)
//        binding.cardOption2.setCardBackgroundColor(defaultColor)
//        binding.cardOption3.setCardBackgroundColor(defaultColor)
//        binding.cardOption4.setCardBackgroundColor(defaultColor)
    }
}