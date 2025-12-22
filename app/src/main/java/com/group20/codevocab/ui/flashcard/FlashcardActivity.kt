package com.group20.codevocab.ui.flashcard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.group20.codevocab.databinding.ActivityFlashcardBinding
import com.group20.codevocab.model.WordItem
import com.group20.codevocab.viewmodel.FlashcardViewModel
import com.group20.codevocab.viewmodel.FlashcardViewModelFactory
import com.group20.codevocab.viewmodel.WordListState
import com.group20.codevocab.viewmodel.WordViewModel
import com.group20.codevocab.viewmodel.WordViewModelFactory
import kotlinx.coroutines.launch

class FlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardBinding
    private lateinit var flashViewModel: FlashcardViewModel
    private lateinit var wordViewModel: WordViewModel

    private var currentIndex = 0
    private var vocabList = emptyList<WordItem>()
    private var showFront = true
    private var moduleId: String? = ""

    // Biến đếm trong session để đảm bảo chính xác khi chuyển màn hình
    private var sessionKnow = 0
    private var sessionHard = 0
    private var sessionReview = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moduleId = intent.getStringExtra("module_id")
        val moduleName = intent.getStringExtra("module_name")
        val isLocal = intent.getBooleanExtra("is_local", false)

        if (moduleId == null) {
            Toast.makeText(this, "Module not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val flashFactory = FlashcardViewModelFactory(applicationContext)
        flashViewModel = ViewModelProvider(this, flashFactory)[FlashcardViewModel::class.java]

        val wordFactory = WordViewModelFactory(applicationContext)
        wordViewModel = ViewModelProvider(this, wordFactory)[WordViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.state.collect { state ->
                    when (state) {
                        is WordListState.Success -> {
                            vocabList = state.items
                            currentIndex = 0
                            if (vocabList.isNotEmpty()) {
                                updateProgressUI()
                                showFlashcard(currentIndex)
                            } else {
                                Toast.makeText(this@FlashcardActivity, "No words in this module", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        is WordListState.Loading -> {
                            // Data is being loaded, can show a progress bar
                        }
                        is WordListState.Error -> {
                            Toast.makeText(this@FlashcardActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        if (isLocal) {
            wordViewModel.loadWords(moduleId!!, moduleName)
        } else {
            wordViewModel.loadWordsFromServer(moduleId!!, moduleName)
        }

        setupUI()
    }

    private fun setupUI() {
        binding.cardFlash.setOnClickListener {
            toggleCard()
        }

        binding.btnKnow.setOnClickListener { submitAnswer(FlashcardViewModel.FlashcardStatus.KNOW) }
        binding.btnReview.setOnClickListener { submitAnswer(FlashcardViewModel.FlashcardStatus.REVIEW) }
        binding.btnHard.setOnClickListener { submitAnswer(FlashcardViewModel.FlashcardStatus.HARD) }

        binding.btnBackCard.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateProgressUI()
                showFlashcard(currentIndex)
            } else {
                finish()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun updateProgressUI() {
        val total = vocabList.size
        val current = currentIndex + 1

        binding.thanhTienDo.max = total
        binding.thanhTienDo.progress = current
        binding.tvProgressCount.text = "$current of $total"
    }

    private fun toggleCard() {
        if (showFront) {
            binding.tvWord.visibility = View.GONE
            binding.cardBackLayout.visibility = View.VISIBLE
        } else {
            binding.tvWord.visibility = View.VISIBLE
            binding.cardBackLayout.visibility = View.GONE
        }
        showFront = !showFront
    }

    private fun submitAnswer(status: FlashcardViewModel.FlashcardStatus) {
        val currentWord = vocabList.getOrNull(currentIndex) ?: return

        // 1. Lưu vào Database thông qua ViewModel
        flashViewModel.updateStatus(
            vocabId = currentWord.id ?: "0",
            moduleId = moduleId ?: "0",
            status = status
        )

        // 2. Cập nhật biến đếm session tại chỗ
        when (status) {
            FlashcardViewModel.FlashcardStatus.KNOW -> sessionKnow++
            FlashcardViewModel.FlashcardStatus.HARD -> sessionHard++
            FlashcardViewModel.FlashcardStatus.REVIEW -> sessionReview++
        }

        currentIndex++
        if (currentIndex < vocabList.size) {
            updateProgressUI()
            showFlashcard(currentIndex)
        } else {
            // 3. Chuyển dữ liệu sang màn hình tổng kết
            val intent = Intent(this, FlashcardSummaryActivity::class.java).apply {
                putExtra("TOTAL_COUNT", vocabList.size)
                putExtra("KNOW_COUNT", sessionKnow)
                putExtra("HARD_COUNT", sessionHard)
                putExtra("REVIEW_COUNT", sessionReview)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun showFlashcard(index: Int) {
        val vocab = vocabList[index]
        binding.tvWord.text = vocab.textEn
        binding.tvMeaning.text = vocab.meaningVi
        binding.tvExample.text = vocab.exampleSentence

        showFront = true
        binding.tvWord.visibility = View.VISIBLE
        binding.cardBackLayout.visibility = View.GONE
    }
}