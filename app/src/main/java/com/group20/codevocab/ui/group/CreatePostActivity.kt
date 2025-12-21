package com.group20.codevocab.ui.group

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.group20.codevocab.databinding.ActivityCreatePostBinding

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnPost.setOnClickListener {
            // TODO: Implement logic to save the new post to the group
            finish()
        }

        binding.btnSelectModule.setOnClickListener {
            // TODO: Open a dialog or activity to select a module from the user's collection
        }
    }
}
