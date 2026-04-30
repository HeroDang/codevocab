package com.group20.codevocab.ui.flashcard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.group20.codevocab.model.ReviewableWord
import com.group20.codevocab.databinding.ActivityFlashcardSummaryBinding

class FlashcardSummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlashcardSummaryBinding

    private var hardWords: ArrayList<ReviewableWord>? = null
    private var reviewWords: ArrayList<ReviewableWord>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hardWords = intent.getParcelableArrayListExtra("HARD_WORDS")
        reviewWords = intent.getParcelableArrayListExtra("REVIEW_WORDS")

        displayStats()
        setupListeners()
    }

    private fun displayStats() {
        val total = intent.getIntExtra("TOTAL_COUNT", 0)
        val know = intent.getIntExtra("KNOW_COUNT", 0)
        val hard = intent.getIntExtra("HARD_COUNT", 0)
        val review = intent.getIntExtra("REVIEW_COUNT", 0)

        binding.tvTotalCardsVal.text = total.toString()
        binding.tvKnowCount.text = "$know cards"
        binding.tvHardCount.text = "$hard cards"
        binding.tvReviewCount.text = "$review cards"

        val accuracy = if (total > 0) (know * 100 / total) else 0
        binding.tvAccuracyVal.text = "$accuracy%"

        binding.flashcardChart.setData(know, hard, review)

        val retryCount = (hardWords?.size ?: 0) + (reviewWords?.size ?: 0)
        if (retryCount > 0) {
            binding.tvTipDesc.text = "You have $retryCount cards that need more practice. Consider reviewing them again to improve retention."
            binding.btnRetry.isEnabled = true
        } else {
            binding.tvTipDesc.text = "Excellent! You've mastered all the cards in this session. Keep up the great work!"
            binding.btnRetry.isEnabled = false
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnFinish.setOnClickListener {
            finish()
        }

        binding.btnRetry.setOnClickListener {
            val retryWordsList = ArrayList<ReviewableWord>()
            hardWords?.let { retryWordsList.addAll(it) }
            reviewWords?.let { retryWordsList.addAll(it) }

            if (retryWordsList.isNotEmpty()) {
                val intent = Intent(this, FlashcardActivity::class.java).apply {
                    putParcelableArrayListExtra("EXTRA_RETRY_WORDS", retryWordsList)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                startActivity(intent)
            }
            finish()
        }
    }
}
