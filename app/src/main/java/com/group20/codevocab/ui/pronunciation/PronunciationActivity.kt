package com.group20.codevocab.ui.pronunciation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ActivityPronunciationPracticeBinding

class PronunciationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPronunciationPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pronunciation_practice)

        // TODO: Get vocab/module data from intent

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        // TODO: Add listeners for play, record, next, and try again buttons
    }
}
