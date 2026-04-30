package com.group20.codevocab.ui.module

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.data.repository.SpeakingPracticeRepository
import com.group20.codevocab.databinding.ActivitySpeakingPracticeBinding
import com.group20.codevocab.ui.common.speaker.Speaker
import com.group20.codevocab.ui.common.speaker.SpeakerFactory
import com.group20.codevocab.utils.PreferenceManager
import com.group20.codevocab.utils.SpeechToTextManager
import com.group20.codevocab.viewmodel.SpeakingPracticeState
import com.group20.codevocab.viewmodel.SpeakingPracticeViewModel
import com.group20.codevocab.viewmodel.SpeakingPracticeViewModelFactory
import kotlinx.coroutines.launch

data class SpeakingSentence(
    val english: String,
    val phonetics: String
)

class SpeakingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpeakingPracticeBinding
    private lateinit var speaker: Speaker
    private lateinit var sttManager: SpeechToTextManager
    private var currentIndex = 0
    private var sentences: List<SpeakingSentence> = emptyList()
    private val handler = Handler(Looper.getMainLooper())

    private val viewModel: SpeakingPracticeViewModel by viewModels {
        val authRepository = AuthRepository(ApiClient.api)
        val speakingRepository = SpeakingPracticeRepository()
        SpeakingPracticeViewModelFactory(speakingRepository, authRepository)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startSTT()
        } else {
            Toast.makeText(this, "Microphone permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeakingPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speaker = SpeakerFactory.create(this)
        sttManager = SpeechToTextManager(this)

        val moduleId = intent.getStringExtra("module_id") ?: ""
        
        setupListeners()
        observeViewModel()

        if (moduleId.isNotEmpty()) {
            viewModel.loadSentences(moduleId)
        } else {
            Toast.makeText(this, "Module ID missing", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is SpeakingPracticeState.Loading -> {
                            binding.loadingOverlay.visibility = View.VISIBLE
                            binding.scrollView.visibility = View.GONE
                        }
                        is SpeakingPracticeState.Success -> {
                            binding.loadingOverlay.visibility = View.GONE
                            binding.scrollView.visibility = View.VISIBLE
                            
                            sentences = state.sentences.map { 
                                SpeakingSentence(it.text, it.phonetics) 
                            }
                            if (sentences.isNotEmpty()) {
                                currentIndex = 0
                                setupUI()
                            } else {
                                Toast.makeText(this@SpeakingActivity, "No sentences found", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        is SpeakingPracticeState.Error -> {
                            binding.loadingOverlay.visibility = View.GONE
                            Toast.makeText(this@SpeakingActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        if (sentences.isEmpty()) return
        updateSentence()
        binding.progressBar.max = sentences.size
        binding.progressBar.progress = currentIndex + 1
        binding.tvProgressCount.text = "${currentIndex + 1}/${sentences.size} sentences"
    }

    private fun updateSentence() {
        val sentence = sentences[currentIndex]
        binding.tvSentence.text = sentence.english
        binding.tvTranslation.text = sentence.phonetics
        binding.cardScore.visibility = View.GONE
        
        if (currentIndex == sentences.size - 1) {
            binding.btnNext.text = "Finish"
        } else {
            binding.btnNext.text = "Next Sentence"
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnListen.setOnClickListener {
            if (sentences.isNotEmpty()) {
                speaker.speak(sentences[currentIndex].english)
            }
        }

        binding.btnRecord.setOnClickListener {
            checkPermissionAndStartSTT()
        }

        binding.btnNext.setOnClickListener {
            if (currentIndex < sentences.size - 1) {
                currentIndex++
                updateSentence()
                binding.progressBar.progress = currentIndex + 1
                binding.tvProgressCount.text = "${currentIndex + 1}/${sentences.size} sentences"
            } else {
                navigateToSummary()
            }
        }
    }

    private fun checkPermissionAndStartSTT() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startSTT()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startSTT() {
        handler.removeCallbacksAndMessages(null)
        
        sttManager.startListening(
            onStatusChange = { status ->
                binding.tvStatus.text = status
            },
            onResult = { result ->
                handler.removeCallbacksAndMessages(null)
                binding.tvStatus.text = "Tap to record"
                showScore(result)
            },
            onError = { error ->
                handler.removeCallbacksAndMessages(null)
                binding.tvStatus.text = "Tap to record"
                // Nếu không nhận diện được gì, thông báo cho user
                if (error == "No speech input") {
                    Toast.makeText(this, "Không nghe thấy tiếng bạn. Vui lòng đọc lại!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Tự động dừng ghi âm sau 10 giây
        handler.postDelayed({
            sttManager.stopListening()
            if (binding.tvStatus.text == "Listening...") {
                binding.tvStatus.text = "Time's up!"
                Toast.makeText(this, "Bạn không nói gì sau 10 giây. Vui lòng thử lại!", Toast.LENGTH_LONG).show()
            }
        }, 10000)
    }

    private fun navigateToSummary() {
        val intent = Intent(this, SpeakingSummaryActivity::class.java).apply {
            putExtra("AVERAGE_SCORE", 92)
            putExtra("TOTAL_SENTENCES", sentences.size)
            putExtra("HIGH_ACCURACY", (sentences.size * 0.7).toInt() + 1)
            putExtra("NEEDS_PRACTICE", (sentences.size * 0.3).toInt())
        }
        startActivity(intent)
        finish()
    }

    private fun showScore(recognizedText: String) {
        val targetText = sentences[currentIndex].english
        
        // Kiểm tra nếu độ dài kết quả nhận diện < 70% độ dài câu gốc
        if (recognizedText.isEmpty() || recognizedText.length < targetText.length * 0.5) {
            Toast.makeText(this, "Vui lòng đọc lại rõ ràng hơn!", Toast.LENGTH_LONG).show()
            binding.cardScore.visibility = View.GONE
            return
        }

        binding.cardScore.visibility = View.VISIBLE
        binding.tvScore.text = "Recognized Text"
        binding.tvFeedback.text = recognizedText
        
        val cleanTarget = targetText.lowercase().replace(Regex("[^a-z ]"), "").trim()
        val cleanRecognized = recognizedText.lowercase().replace(Regex("[^a-z ]"), "").trim()
        
        if (cleanRecognized == cleanTarget) {
            binding.tvScore.text = "Score: 100/100"
            binding.tvFeedback.text = "Perfect! \"$recognizedText\""
        } else {
            binding.tvScore.text = "Score: 80/100"
            binding.tvFeedback.text = "You said: \"$recognizedText\""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        if (::speaker.isInitialized) {
            speaker.shutdown()
        }
        sttManager.destroy()
    }
}
