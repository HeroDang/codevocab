package com.group20.codevocab.ui.flashcard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.group20.codevocab.data.local.AppDatabase
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity
import com.group20.codevocab.data.local.entity.VocabularyEntity
import com.group20.codevocab.data.repository.FlashcardProgressRepository
import com.group20.codevocab.data.repository.VocabularyRepository
import com.group20.codevocab.databinding.ActivityFlashcardBinding
import com.group20.codevocab.viewmodel.FlashcardViewModel
import com.group20.codevocab.viewmodel.FlashcardViewModelFactory

class FlashcardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlashcardBinding
    private lateinit var viewModel: FlashcardViewModel
    private lateinit var adapter: FlashcardAdapter

    private var currentIndex = 0
    private var vocabList = listOf<Pair<VocabularyEntity, FlashcardProgressEntity?>>()
    private var showFront = true
    private var moduleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Lấy moduleId được truyền sang
        moduleId = intent.getIntExtra("module_id", -1)
        if (moduleId == -1) {
            Toast.makeText(this, "Module ID không hợp lệ", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val factory = FlashcardViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[FlashcardViewModel::class.java]

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
        viewModel.vocabList.observe(this) {
            vocabList = it
            showFlashcard(0)
        }

        // 🔹 Tải dữ liệu từ module
        viewModel.loadVocabWithProgress(moduleId)

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
    }

    private fun toggleCard() {
        if (showFront) {
            binding.tvWord.visibility = View.GONE
            binding.backSide.visibility = View.VISIBLE
        } else {
            binding.tvWord.visibility = View.VISIBLE
            binding.backSide.visibility = View.GONE
        }
        showFront = !showFront
    }

    private fun submitAnswer(isKnown: Boolean) {
        val current = vocabList.getOrNull(currentIndex) ?: return
        val vocab = current.first
        viewModel.markKnown(vocab.id, isKnown, moduleId)

        currentIndex++
        if (currentIndex < vocabList.size) {
            showFlashcard(currentIndex)
        } else {
            // Chuyển sang màn hình tổng kết
//            startActivity(Intent(this, FlashcardSummaryActivity::class.java))
//            finish()
            Toast.makeText(applicationContext, "Chuyển sang màn hình tổng kết", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFlashcard(index: Int) {
        val (vocab, _) = vocabList[index]
        binding.tvWord.text = vocab.word
        binding.tvMeaning.text = vocab.meaningVi
        binding.tvExample.text = vocab.example

        // Reset card to front side
        showFront = true
        binding.tvWord.visibility = View.VISIBLE
        binding.backSide.visibility = View.GONE
    }
}
