package com.group20.codevocab.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.group20.codevocab.R
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.databinding.ActivityEditProfileBinding
import com.group20.codevocab.viewmodel.BaseViewModelFactory
import com.group20.codevocab.viewmodel.UserViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        observeUserData()
        setupListeners()
        
        userViewModel.fetchCurrentUser()
    }

    private fun setupViewModel() {
        val apiService = ApiClient.getApiService()
        val repository = AuthRepository(apiService)
        val factory = object : BaseViewModelFactory<UserViewModel>() {
            override fun createViewModel(): UserViewModel = UserViewModel(repository)
        }
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
    }

    private fun observeUserData() {
        userViewModel.userData.observe(this) { user ->
            user?.let {
                binding.etFullName.setText(it.name)
                binding.etEmail.setText(it.email)
                
                if (!it.avatarUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(it.avatarUrl)
                        .placeholder(R.drawable.ic_user)
                        .circleCrop()
                        .into(binding.ivUserAvatar)
                }
            }
        }

        userViewModel.updateStatus.observe(this) { isSuccess ->
            if (isGranted()) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                finish() // Quay lại sau khi lưu thành công
            }
        }

        userViewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isGranted(): Boolean {
        return userViewModel.updateStatus.value ?: false
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                userViewModel.updateProfile(name, email)
            } else {
                Toast.makeText(this, "Please fill in Name and Email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnChangeAvatar.setOnClickListener {
            Toast.makeText(this, "Change avatar feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        binding.tvRemovePhoto.setOnClickListener {
            binding.ivUserAvatar.setImageResource(R.drawable.ic_user)
        }
    }
}
