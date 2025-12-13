package com.group20.codevocab.ui.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ActivityQuizSummaryBinding

class QuizSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_summary)

        // Retrieve data from intent
        val score = intent.getIntExtra("SCORE", 0)
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)

        // Calculate percentage and incorrect answers
        val percentage = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
        val incorrectCount = totalQuestions - score

        // Set the data to the views
        binding.tvScorePercent.text = "$percentage%"
        binding.tvScoreDetail.text = "$score out of $totalQuestions correct"
        binding.tvCorrectCount.text = score.toString()
        binding.tvIncorrectCount.text = incorrectCount.toString()

        // Setup button listeners
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish() // Go back to the previous screen
        }

        binding.btnFinish.setOnClickListener {
            // TODO: Implement what happens when the user finishes
            // For example, navigate back to the main screen
            finish()
        }

        binding.btnRetryMistakes.setOnClickListener {
            // TODO: Implement logic to retry only the incorrect questions
        }

        binding.btnReviewAll.setOnClickListener {
            // TODO: Implement logic to review all answers
        }
    }
}