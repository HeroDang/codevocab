package com.group20.codevocab.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.group20.codevocab.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Chuyển về màn Login khi nhấn Sign In
        binding.tvSignIn.setOnClickListener {
            finish()
        }

        binding.btnBackToOnboarding.setOnClickListener {
            finish()
        }
    }
}