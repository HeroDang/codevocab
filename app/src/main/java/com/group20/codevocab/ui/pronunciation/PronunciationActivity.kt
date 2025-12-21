package com.group20.codevocab.ui.pronunciation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ActivityFlashcardBinding
import com.group20.codevocab.databinding.ActivityPronunciationPracticeBinding
import com.group20.codevocab.model.WordItem
import com.group20.codevocab.viewmodel.WordListState
import com.group20.codevocab.viewmodel.WordViewModel
import com.group20.codevocab.viewmodel.WordViewModelFactory
import kotlinx.coroutines.launch

class PronunciationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPronunciationPracticeBinding
    private lateinit var wordViewModel: WordViewModel
    private var currentIndex = 0
    private var vocabList = emptyList<WordItem>()
    private var showFront = true
    private var moduleId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_pronunciation_practice)
        binding = ActivityPronunciationPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moduleId = intent.getStringExtra("module_id")
        if (moduleId == null) {
            Toast.makeText(this, "Module not found", Toast.LENGTH_SHORT).show()
//            finish()
            return
        }

        // TODO: Get vocab/module data from intent
        val factory = WordViewModelFactory(applicationContext)
        wordViewModel = ViewModelProvider(this, factory)[WordViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.state.collect { state ->
                    when (state) {
                        is WordListState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            wordViewModel.loadWordsFromServer(
                                subModuleId = moduleId!!,
                                subModuleName = null
                            )
                        }

                        is WordListState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            vocabList = state.items
                            currentIndex = 0
                            showPronunciationWord(currentIndex)
                        }

                        is WordListState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@PronunciationActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        // TODO: Add listeners for play, record, next, and try again buttons
    }

    private fun showPronunciationWord(index: Int) {
        val word = vocabList[index]

        binding.tvWordToPractice.text = word.textEn
        binding.tvPhonetics.text = word.ipa ?: ""
        binding.tvDefinition.text = word.meaningVi
//
//        binding.tvWordIndex.text = "${index + 1}/${vocabList.size}"
    }
}
