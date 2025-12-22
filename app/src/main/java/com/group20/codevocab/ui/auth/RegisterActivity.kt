package com.group20.codevocab.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.databinding.ActivityRegisterBinding
import com.group20.codevocab.viewmodel.AuthViewModel
import com.group20.codevocab.viewmodel.BaseViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        val apiService = ApiClient.getApiService()
        val repository = AuthRepository(apiService)
        val factory = object : BaseViewModelFactory<AuthViewModel>() {
            override fun createViewModel(): AuthViewModel = AuthViewModel(repository)
        }
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.registerResult.observe(this) { response ->
            if (response.isSuccessful) {
                Toast.makeText(this, "Account created! Please sign in.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnSignUp.isEnabled = !isLoading
            binding.btnSignUp.text = if (isLoading) "Creating Account..." else "Sign up"
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnSignUp.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.register(email, name, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignIn.setOnClickListener {
            finish()
        }

        binding.btnBackToOnboarding.setOnClickListener {
            finish()
        }
    }
}
