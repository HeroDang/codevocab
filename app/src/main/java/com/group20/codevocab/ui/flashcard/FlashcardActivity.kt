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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity
import com.group20.codevocab.data.local.entity.VocabularyEntity
import com.group20.codevocab.data.repository.FlashcardProgressRepository
import com.group20.codevocab.data.repository.VocabularyRepository
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
//    private lateinit var viewModel: FlashcardViewModel
    private lateinit var wordViewModel: WordViewModel
    private lateinit var adapter: FlashcardAdapter

    private var currentIndex = 0
//    private var vocabList = listOf<Pair<VocabularyEntity, FlashcardProgressEntity?>>()
    private var vocabList = emptyList<WordItem>()
    private var showFront = true
    private var moduleId: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Lấy moduleId được truyền sang
        moduleId = intent.getStringExtra("module_id")
        if (moduleId == null) {
            Toast.makeText(this, "Module not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

//        val factory = FlashcardViewModelFactory(applicationContext)
//        viewModel = ViewModelProvider(this, factory)[FlashcardViewModel::class.java]

        val factory = WordViewModelFactory(applicationContext)
        wordViewModel = ViewModelProvider(this, factory)[WordViewModel::class.java]

        // 🔹 Thiết lập RecyclerView hoặc ViewPager2 để hiển thị flashcard
//        adapter = FlashcardAdapter { flash ->
//            viewModel.markKnown(flash.id, !flash.isKnown, moduleId)
//        }
//        binding.rvFlashcards.adapter = adapter
//        binding.rvFlashcards.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 🔹 Quan sát danh sách từ
//        viewModel.vocabList.observe(this) { list ->
//            adapter.submitList(list)
//        }
//        viewModel.vocabList.observe(this) {
//            vocabList = it
//            showFlashcard(0)
//        }
//
//        // 🔹 Tải dữ liệu từ module
//        viewModel.loadVocabWithProgress(moduleId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.state.collect { state ->
                    when (state) {
                        is WordListState.Success -> {
                            vocabList = state.items
                            currentIndex = 0
                            showFlashcard(currentIndex)
                        }

                        is WordListState.Loading -> {
                            // ⚠️ CHỈ load nếu chưa có data
                            wordViewModel.loadWordsFromServer(
                                subModuleId = moduleId.toString(),
                                subModuleName = null
                            )
                        }

                        is WordListState.Error -> {
                            Toast.makeText(
                                this@FlashcardActivity,
                                state.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }



        setupUI()
    }

    private fun setupUI() {
        binding.cardFlash.setOnClickListener {
            toggleCard()
        }

        binding.btnKnow.setOnClickListener { submitAnswer(true) }
        binding.btnReview.setOnClickListener { submitAnswer(false) }
        binding.btnHard.setOnClickListener { submitAnswer(false) }

        // Nút quay lại
        binding.btnBackCard.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                showFlashcard(currentIndex)
            } else {
                // Nếu đang ở thẻ đầu → quay về ModuleDetail
                finish()
            }
        }

        binding.btnBack.setOnClickListener {
                finish()
        }
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

    private fun submitAnswer(isKnown: Boolean) {
        // TODO Phase sau: lưu progress
//        val current = vocabList.getOrNull(currentIndex) ?: return
//        val vocab = current.first
//        viewModel.markKnown(vocab.id, isKnown, moduleId)
//
        currentIndex++
        if (currentIndex < vocabList.size) {
            showFlashcard(currentIndex)
        } else {
            // Chuyển sang màn hình tổng kết
            Toast.makeText(applicationContext, "Chuyển sang màn hình tổng kết", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, FlashcardSummaryActivity::class.java))
            finish()
        }
    }

    private fun showFlashcard(index: Int) {
//        val (vocab, _) = vocabList[index]
//        binding.tvWord.text = vocab.word
//        binding.tvMeaning.text = vocab.meaningVi
//        binding.tvExample.text = vocab.example

        val vocab = vocabList[index]
        binding.tvWord.text = vocab.textEn
        binding.tvMeaning.text = vocab.meaningVi
        binding.tvExample.text = vocab.exampleSentence


        // Reset card to front side
        showFront = true
        binding.tvWord.visibility = View.VISIBLE
        binding.cardBackLayout.visibility = View.GONE
    }
}
