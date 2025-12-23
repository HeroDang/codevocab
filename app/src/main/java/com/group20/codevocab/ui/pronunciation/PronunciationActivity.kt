package com.group20.codevocab.ui.pronunciation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.group20.codevocab.R
import com.group20.codevocab.databinding.ActivityPronunciationPracticeBinding
import com.group20.codevocab.model.WordItem
import com.group20.codevocab.ui.common.speaker.Speaker
import com.group20.codevocab.ui.common.speaker.SpeakerFactory
import com.group20.codevocab.viewmodel.PronunciationState
import com.group20.codevocab.viewmodel.PronunciationViewModel
import com.group20.codevocab.viewmodel.PronunciationViewModelFactory
import com.group20.codevocab.viewmodel.WordListState
import com.group20.codevocab.viewmodel.WordViewModel
import com.group20.codevocab.viewmodel.WordViewModelFactory
import kotlinx.coroutines.launch

class PronunciationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPronunciationPracticeBinding
    private lateinit var wordViewModel: WordViewModel
    private lateinit var pronunciationViewModel: PronunciationViewModel
    private lateinit var speaker: Speaker
    private var currentIndex = 0
    private var vocabList = emptyList<WordItem>()
    private var isRecording = false
    private var countDownTimer: CountDownTimer? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startRecording()
        } else {
            Toast.makeText(this, "Microphone permission is required to record audio", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPronunciationPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        speaker = SpeakerFactory.create(this)

        val moduleId = intent.getStringExtra("module_id")
        val moduleName = intent.getStringExtra("module_name")
        val isLocal = intent.getBooleanExtra("is_local", false)

        if (moduleId == null) {
            Toast.makeText(this, "Module not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize ViewModels
        val wordFactory = WordViewModelFactory(applicationContext)
        wordViewModel = ViewModelProvider(this, wordFactory)[WordViewModel::class.java]

        val pronunciationFactory = PronunciationViewModelFactory(applicationContext)
        pronunciationViewModel = ViewModelProvider(this, pronunciationFactory)[PronunciationViewModel::class.java]

        // Observe Word List State
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.state.collect { state ->
                    when (state) {
                        is WordListState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is WordListState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            vocabList = state.items
                            currentIndex = 0
                            if (vocabList.isNotEmpty()) {
                                updateProgressUI()
                                showPronunciationWord(currentIndex)
                            } else {
                                Toast.makeText(this@PronunciationActivity, "No words to practice.", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        is WordListState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@PronunciationActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // Observe Pronunciation State
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pronunciationViewModel.state.collect { state ->
                    when (state) {
                        is PronunciationState.Idle -> {
                            updateRecordButtonState(isRecording = false)
                            // Text sẽ được reset bởi CountDownTimer khi hoàn tất hoặc hủy
                        }
                        is PronunciationState.Recording -> {
                            updateRecordButtonState(isRecording = true)
                            // Start countdown UI update
                            startCountDown()
                            binding.progressBarProcessing.visibility = View.GONE
                            binding.cardScore.visibility = View.GONE
                        }
                        is PronunciationState.Processing -> {
                            updateRecordButtonState(isRecording = false)
                            binding.tvRecordingTime.text = "Processing..."
                            binding.progressBarProcessing.visibility = View.VISIBLE
                            binding.cardScore.visibility = View.GONE
                        }
                        is PronunciationState.Success -> {
                            updateRecordButtonState(isRecording = false)
                            binding.tvRecordingTime.text = "Tap to record"
                            binding.progressBarProcessing.visibility = View.GONE
                            showScoreResult(state.score, state.recognized)
                        }
                        is PronunciationState.Error -> {
                            updateRecordButtonState(isRecording = false)
                            binding.tvRecordingTime.text = "Error"
                            binding.progressBarProcessing.visibility = View.GONE
                            binding.cardScore.visibility = View.GONE
                            Toast.makeText(this@PronunciationActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // Load Words
        if (isLocal) {
            wordViewModel.loadWords(moduleId, moduleName)
        } else {
            wordViewModel.loadWordsFromServer(moduleId, moduleName)
        }

        setupListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::speaker.isInitialized) {
            speaker.shutdown()
        }
        countDownTimer?.cancel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnNextWord.setOnClickListener {
            if (vocabList.isNotEmpty()) {
                if (currentIndex < vocabList.size - 1) {
                    currentIndex++
                    updateProgressUI()
                    showPronunciationWord(currentIndex)
                    resetScoreUI()
                } else {
                    Toast.makeText(this, "You have completed all words!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.btnTryAgain.setOnClickListener {
            resetScoreUI()
        }

        binding.btnPlaySample.setOnClickListener {
            if (vocabList.isNotEmpty()) {
                val currentWord = vocabList[currentIndex]
                speaker.speak(currentWord.textEn)
            }
        }

        binding.btnRecord.setOnClickListener {
            if (!isRecording) {
                checkPermissionAndStartRecording()
            } else {
                // Nếu đang ghi âm mà bấm lần nữa thì không làm gì (hoặc có thể cho phép dừng sớm nếu muốn, 
                // nhưng yêu cầu là "tự động call api sau 5s" nên ta cứ để nó chạy hết 5s)
                Toast.makeText(this, "Recording in progress...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionAndStartRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startRecording()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startRecording() {
        if (vocabList.isEmpty()) return
        val targetWord = vocabList[currentIndex].textEn
        
        // Bắt đầu ghi âm qua ViewModel
        pronunciationViewModel.startRecording(targetWord)
    }
    
    private fun startCountDown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvRecordingTime.text = "Recording... ${secondsRemaining + 1}s"
            }

            override fun onFinish() {
                 binding.tvRecordingTime.text = "Finishing..."
            }
        }.start()
    }

    private fun updateRecordButtonState(isRecording: Boolean) {
        this.isRecording = isRecording
        if (isRecording) {
            binding.btnRecord.setImageResource(R.drawable.ic_stop_circle) // Optional: change icon to indicate active state
            binding.btnRecord.backgroundTintList = ContextCompat.getColorStateList(this, R.color.status_red)
        } else {
            binding.btnRecord.setImageResource(R.drawable.ic_mic)
            binding.btnRecord.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)
        }
    }

    private fun updateProgressUI() {
        val total = vocabList.size
        val current = currentIndex + 1
        binding.progressBar.max = total
        binding.progressBar.progress = current
        binding.tvProgressCount.text = "$current/$total"
    }

    private fun resetScoreUI() {
        binding.cardScore.visibility = View.GONE
        binding.progressBarProcessing.visibility = View.GONE
        binding.tvRecordingTime.text = "Tap to record"
        
        // Reset state trong ViewModel để lần sau không bị nhảy vào trạng thái cũ
        // Tuy nhiên PronunciationState là flow, nên khi startRecording mới nó sẽ tự update
    }

    private fun showPronunciationWord(index: Int) {
        val word = vocabList[index]
        binding.tvWordToPractice.text = word.textEn
        binding.tvPhonetics.text = word.ipa ?: ""
        binding.tvDefinition.text = word.meaningVi
    }
    
    private fun showScoreResult(score: Float, recognizedText: String) {
        binding.cardScore.visibility = View.VISIBLE
        
        // Convert score 0-1 to 0-100 integer
        val scoreInt = (score * 100).toInt()
        binding.tvScore.text = scoreInt.toString()
        
        // Update feedback text based on score
        binding.tvScoreFeedback2.text = "Recognized: $recognizedText"
        
        if (score > 0.8) {
             binding.tvScoreFeedback1.text = "Excellent!"
             binding.tvScore.setTextColor(ContextCompat.getColor(this, R.color.green_700))
        } else if (score > 0.5) {
             binding.tvScoreFeedback1.text = "Good job!"
             binding.tvScore.setTextColor(ContextCompat.getColor(this, R.color.yellow_600))
        } else {
             binding.tvScoreFeedback1.text = "Try again"
             binding.tvScore.setTextColor(ContextCompat.getColor(this, R.color.status_red))
        }
    }
}