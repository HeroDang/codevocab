package com.group20.codevocab.ui.flashcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.group20.codevocab.databinding.ActivityFlashcardSummaryBinding

class FlashcardSummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlashcardSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        displayStats()
        setupListeners()
    }

    private fun displayStats() {
        val total = intent.getIntExtra("TOTAL_COUNT", 0)
        val know = intent.getIntExtra("KNOW_COUNT", 0)
        val hard = intent.getIntExtra("HARD_COUNT", 0)
        val review = intent.getIntExtra("REVIEW_COUNT", 0)

        // Cập nhật text thông thường
        binding.tvTotalCardsVal.text = total.toString()
        binding.tvKnowCount.text = "$know cards"
        binding.tvHardCount.text = "$hard cards"
        binding.tvReviewCount.text = "$review cards"

        val accuracy = if (total > 0) (know * 100 / total) else 0
        binding.tvAccuracyVal.text = "$accuracy%"

        // Cập nhật biểu đồ
        binding.flashcardChart.setData(know, hard, review)
        
        if (hard + review > 0) {
            binding.tvTipDesc.text = "You have ${hard + review} cards that need more practice. Consider reviewing them again to improve retention."
        } else {
            binding.tvTipDesc.text = "Excellent! You've mastered all the cards in this session. Keep up the great work!"
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
            finish()
        }
    }
}
