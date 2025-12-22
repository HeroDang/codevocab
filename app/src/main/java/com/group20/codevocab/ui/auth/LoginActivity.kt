package com.group20.codevocab.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.MainActivity
import com.group20.codevocab.data.local.TokenManager
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.remote.dto.LoginResponse
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.databinding.ActivityLoginBinding
import com.group20.codevocab.viewmodel.AuthViewModel
import com.group20.codevocab.viewmodel.BaseViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        val apiService = ApiClient.getApiService()
        val repository = AuthRepository(apiService)

        val factory = object : BaseViewModelFactory<AuthViewModel>() {
            override fun createViewModel(): AuthViewModel {
                return AuthViewModel(repository)
            }
        }

        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { response ->
            if (response.isSuccessful) {
                val loginResponse = response.body()

                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                // ✅ Lưu token thông qua TokenManager và khởi tạo ApiClient
                loginResponse?.let {
                    val tokenManager = TokenManager(this)
                    tokenManager.saveToken(it.accessToken)
                    ApiClient.init(this)
                }

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnSignIn.isEnabled = !isLoading
            binding.btnSignIn.text = if (isLoading) "Signing In..." else "Sign In"
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            val identifier = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (identifier.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(identifier, password)
            } else {
                Toast.makeText(this, "Please enter email/username and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnForgotPassword.setOnClickListener {
            // Logic for forgot password
        }

        binding.btnBackToOnboarding.setOnClickListener {
            finish()
        }
    }
}