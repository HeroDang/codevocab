package com.group20.codevocab.ui.group

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.group20.codevocab.databinding.ActivityCommentListBinding

class CommentListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        
        binding.btnSubmitComment.setOnClickListener {
            // TODO: Logic to post a new comment
            binding.etNewComment.text.clear()
        }
    }
}
