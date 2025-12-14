package com.group20.codevocab.ui.module

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.group20.codevocab.R
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.repository.VocabRepository
import com.group20.codevocab.databinding.ActivityWordListBinding
import com.group20.codevocab.ui.flashcard.FlashcardActivity
import com.group20.codevocab.ui.quiz.QuizActivity
import com.group20.codevocab.viewmodel.WordViewModel
import com.group20.codevocab.viewmodel.WordViewModelFactory

class WordListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordListBinding
    private lateinit var viewModel: WordViewModel
    private lateinit var adapter: WordListAdapter
    private var moduleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moduleId = intent.getIntExtra("module_id", -1)
        if (moduleId == -1) {
            Toast.makeText(this, "Module not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize ViewModel
        val db = AppDatabase.getDatabase(this)
        val repo = VocabRepository(db.vocabDao())
        val factory = WordViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[WordViewModel::class.java]

        setupUI()
        observeData()
        viewModel.loadWords(moduleId)
    }

    private fun setupUI() {
        // Back Button
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            finish() // Quay về Activity trước đó (ModuleDetailActivity)
        }

        // RecyclerView
        adapter = WordListAdapter(emptyList())
        binding.rvWords.layoutManager = LinearLayoutManager(this)
        binding.rvWords.adapter = adapter

        // Button Actions
        binding.btnFlashcards.setOnClickListener {
            val intent = Intent(this, FlashcardActivity::class.java)
            intent.putExtra("module_id", moduleId)
            startActivity(intent)
        }

        binding.btnQuiz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("module_id", moduleId)
            startActivity(intent)
        }

        binding.btnPractice.setOnClickListener {
            // TODO: Implement Practice Activity navigation
             Toast.makeText(this, "Practice feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {
        viewModel.words.observe(this) { wordList ->
            adapter.updateData(wordList)
            binding.tvSubtitle.text = "${wordList.size} words"
        }
    }
}