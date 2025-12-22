package com.group20.codevocab.ui.quiz

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.adapter.MistakesAdapter
import com.group20.codevocab.databinding.ActivityQuizSummaryBinding
import com.group20.codevocab.model.QuizMistake
import com.group20.codevocab.model.QuizQuestion

class QuizSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizSummaryBinding
    private var mistakes: ArrayList<QuizMistake>? = null
    private lateinit var mistakesAdapter: MistakesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val score = intent.getIntExtra("EXTRA_SCORE", 0)
        val totalQuestions = intent.getIntExtra("EXTRA_TOTAL_QUESTIONS", 0)

        mistakes = getParcelableArrayList(intent, "EXTRA_MISTAKES")

        displaySummary(score, totalQuestions)
        setupMistakesReview()
        setupListeners()
    }

    private fun displaySummary(score: Int, totalQuestions: Int) {
        val percentage = if (totalQuestions > 0) (score * 100) / totalQuestions else 0
        val incorrectCount = totalQuestions - score

        binding.tvScorePercent.text = "$percentage%"
        binding.tvScoreDetail.text = "$score out of $totalQuestions correct"
        binding.tvCorrectCount.text = score.toString()
        binding.tvIncorrectCount.text = incorrectCount.toString()
    }

    private fun setupMistakesReview() {
        if (mistakes.isNullOrEmpty()) {
            binding.reviewMistakesSection.visibility = View.GONE
            binding.btnRetryMistakes.visibility = View.GONE
            binding.btnReviewAll.visibility = View.GONE
        } else {
            binding.reviewMistakesSection.visibility = View.VISIBLE
            binding.btnRetryMistakes.visibility = View.VISIBLE
            binding.recyclerViewMistakes.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewMistakes.isNestedScrollingEnabled = false

            mistakesAdapter = MistakesAdapter(arrayListOf())
            binding.recyclerViewMistakes.adapter = mistakesAdapter

            if (mistakes!!.size > 3) {
                mistakesAdapter.updateData(ArrayList(mistakes!!.subList(0, 3)))
                binding.btnReviewAll.visibility = View.VISIBLE
            } else {
                mistakesAdapter.updateData(mistakes!!)
                binding.btnReviewAll.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnFinish.setOnClickListener { finish() }

        binding.btnRetryMistakes.setOnClickListener {
            retryMistakes()
        }

        binding.btnReviewAll.setOnClickListener {
            mistakes?.let {
                mistakesAdapter.updateData(it)
            }
            it.visibility = View.GONE
        }
    }

    private fun retryMistakes() {
        val retryQuestions = mistakes?.map { it.question } as? ArrayList<QuizQuestion>
        if (retryQuestions.isNullOrEmpty()) {
            return
        }

        val intent = Intent(this, QuizActivity::class.java).apply {
            putParcelableArrayListExtra("EXTRA_RETRY_QUESTIONS", retryQuestions)
        }
        startActivity(intent)
        finish()
    }

    private inline fun <reified T : android.os.Parcelable> getParcelableArrayList(intent: Intent, key: String): ArrayList<T>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(key)
        }
    }
}