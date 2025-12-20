package com.group20.codevocab.ui.module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.group20.codevocab.R
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.repository.VocabRepository
import com.group20.codevocab.data.repository.WordRepository
import com.group20.codevocab.databinding.ActivityWordListBinding
import com.group20.codevocab.ui.flashcard.FlashcardActivity
import com.group20.codevocab.ui.quiz.QuizActivity
import com.group20.codevocab.viewmodel.DebugApiViewModel
import com.group20.codevocab.viewmodel.WordListState
import com.group20.codevocab.viewmodel.WordViewModel
import com.group20.codevocab.viewmodel.WordViewModelFactory
import kotlinx.coroutines.launch

class WordListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordListBinding
    private lateinit var viewModel: WordViewModel
    private val debugApiViewModel: DebugApiViewModel by viewModels()
    private lateinit var adapter: WordListAdapter
    private var moduleId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moduleId = intent.getStringExtra("module_id")
        val subModuleName = intent.getStringExtra("module_name")
        if (moduleId == null) {
            Toast.makeText(this, "Module not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize ViewModel
        val factory = WordViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[WordViewModel::class.java]

        setupUI()
        observeData()
//        viewModel.loadWords(moduleId)

        viewModel.loadWordsFromServer(
            subModuleId = moduleId.toString(),
            subModuleName = subModuleName
        )
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
            debugApiViewModel.ping()
        }
    }

    private fun observeData() {
//        viewModel.words.observe(this) { wordList ->
//            adapter.updateData(wordList)
//            binding.tvSubtitle.text = "${wordList.size} words"
//        }

        // ✅ Collect StateFlow đúng cách (chạy theo lifecycle STARTED)
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                debugApiViewModel.text.collect { msg ->
//                    Toast.makeText(this@WordListActivity, msg, Toast.LENGTH_SHORT).show()
//                    Log.d("API_PING", msg)
//                }
//            }
//        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is WordListState.Loading -> {
                            // TODO: show loading nếu muốn
                        }

                        is WordListState.Success -> {
                            adapter.updateData(state.items)
                            binding.tvTitle.text = state.title ?: "Word List"
                            binding.tvSubtitle.text = "${state.items.size} words"
                        }

                        is WordListState.Error -> {
                            Toast.makeText(
                                this@WordListActivity,
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

        // Debug API – giữ nguyên
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                debugApiViewModel.text.collect { msg ->
                    Toast.makeText(this@WordListActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.d("API_PING", msg)
                }
            }
        }

    }
}