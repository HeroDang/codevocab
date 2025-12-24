package com.group20.codevocab.ui.module

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ActivityWordListBinding
import com.group20.codevocab.model.WordItem
import com.group20.codevocab.model.toEntity
import com.group20.codevocab.ui.common.speaker.Speaker
import com.group20.codevocab.ui.common.speaker.SpeakerFactory
import com.group20.codevocab.ui.dictionary.EditWordFragment
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
    private var showMenu: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speaker = SpeakerFactory.create(this)

        moduleId = intent.getStringExtra("module_id")
        moduleName = intent.getStringExtra("module_name")
        isLocal = intent.getBooleanExtra("is_local", false)
        showMenu = intent.getBooleanExtra("show_menu", false)

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

        supportFragmentManager.setFragmentResultListener(EditWordFragment.REQUEST_KEY, this) { _, bundle ->
            val updatedWord = bundle.getParcelable<WordItem>(EditWordFragment.BUNDLE_KEY_UPDATED_WORD)
            if (updatedWord != null) {
                // Save updated word to local database or sync with server as needed
                // Currently just saving to local for simplicity as requested context implies local edits primarily or need implementation
                // If it's remote, we might need a different approach or just update local cache if architecture supports it
                viewModel.saveWords(listOf(updatedWord.toEntity(moduleId!!)))
                
                // Refresh list
                if (isLocal) {
                    viewModel.loadWords(moduleId!!, moduleName)
                } else {
                     // For remote, typically we'd call an API update. 
                     // Assuming for now we reload or update UI directly. 
                     // If offline editing of remote modules isn't fully supported, this might need adjustment.
                     // But let's assume we want to reflect changes.
                     viewModel.loadWordsFromServer(moduleId!!, moduleName)
                }
                Toast.makeText(this, "Word updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        speaker.shutdown()
        super.onDestroy()
    }

    private fun setupUI() {
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener { finish() }

        adapter = WordListAdapter(
            words = emptyList(),
            showMenu = showMenu,
            onSpeakClick = { word ->
                speaker.speak(word.textEn)
            },
            onEditClick = { word ->
                val dialog = EditWordFragment.newInstance(word)
                dialog.show(supportFragmentManager, EditWordFragment.TAG)
            },
            onDeleteClick = { word ->
                // TODO: Implement delete functionality
            }
        )
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
                putExtra("module_name", moduleName)
                putExtra("is_local", isLocal)
            }
            // Toast.makeText(this, "Practice feature coming soon", Toast.LENGTH_SHORT).show()
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
