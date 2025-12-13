package com.group20.codevocab.ui.flashcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ActivityFlashcardSummaryBinding

class FlashcardSummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlashcardSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnFinish.setOnClickListener {
            finish()
        }

        binding.btnRetry.setOnClickListener {
            // TODO: Implement retry logic
        }
    }
}