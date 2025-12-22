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
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ActivityWordListBinding
import com.group20.codevocab.ui.common.speaker.Speaker
import com.group20.codevocab.ui.common.speaker.SpeakerFactory
import com.group20.codevocab.ui.flashcard.FlashcardActivity
import com.group20.codevocab.ui.pronunciation.PronunciationActivity
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
    private lateinit var speaker: Speaker

    private var moduleId: String? = null
    private var moduleName: String? = null
    private var isLocal: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speaker = SpeakerFactory.create(this)

        moduleId = intent.getStringExtra("module_id")
        moduleName = intent.getStringExtra("module_name")
        isLocal = intent.getBooleanExtra("is_local", false)

        if (moduleId == null) {
            Toast.makeText(this, "Module not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val factory = WordViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[WordViewModel::class.java]

        setupUI()
        observeData()

        if (isLocal) {
            viewModel.loadWords(moduleId!!, moduleName)
        } else {
            viewModel.loadWordsFromServer(moduleId!!, moduleName)
        }
    }

    override fun onDestroy() {
        speaker.shutdown()
        super.onDestroy()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = WordListAdapter(emptyList()) { word ->
            speaker.speak(word.textEn)
        }
        binding.rvWords.layoutManager = LinearLayoutManager(this)
        binding.rvWords.adapter = adapter

        binding.btnFlashcards.setOnClickListener {
            val intent = Intent(this, FlashcardActivity::class.java).apply {
                putExtra("module_id", moduleId)
                putExtra("module_name", moduleName)
                putExtra("is_local", isLocal)
            }
            startActivity(intent)
        }

        binding.btnQuiz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("module_id", moduleId)
                putExtra("module_name", moduleName)
                putExtra("is_local", isLocal)
            }
            startActivity(intent)
        }

        binding.btnPractice.setOnClickListener {
            val intent = Intent(this, PronunciationActivity::class.java).apply {
                putExtra("module_id", moduleId)
            }
            Toast.makeText(this, "Practice feature coming soon", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is WordListState.Loading -> {
                            // TODO: show loading
                        }
                        is WordListState.Success -> {
                            adapter.updateData(state.items)
                            binding.tvTitle.text = state.title ?: "Word List"
                            binding.tvSubtitle.text = "${state.items.size} words"
                        }
                        is WordListState.Error -> {
                            Toast.makeText(this@WordListActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

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