package com.group20.codevocab.ui.module

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.group20.codevocab.databinding.ActivitySpeakingSummaryBinding
import com.group20.codevocab.utils.SpeakingSessionManager

class SpeakingSummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpeakingSummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpeakingSummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val averageScore = intent.getIntExtra("AVERAGE_SCORE", 88)
        val totalSentences = intent.getIntExtra("TOTAL_SENTENCES", 10)
        val highAccuracy = intent.getIntExtra("HIGH_ACCURACY", 7)
        val needsPractice = intent.getIntExtra("NEEDS_PRACTICE", 3)

        displaySummary(averageScore, totalSentences, highAccuracy, needsPractice)
        setupMistakesList()
        setupListeners()
    }

    private fun displaySummary(avg: Int, total: Int, high: Int, needs: Int) {
        binding.tvAverageScore.text = "$avg%"
        binding.tvSummaryDetail.text = "You practiced $total sentences"
        binding.tvHighAccuracyCount.text = high.toString()
        binding.tvNeedsPracticeCount.text = needs.toString()
    }

    private fun setupMistakesList() {
        // Lấy dữ liệu từ SpeakingSessionManager và chuyển đổi sang SpeakingMistake
        val sessionResults = SpeakingSessionManager.getResults()
        val mistakes = sessionResults.mapIndexed { index, result ->
            SpeakingMistake(
                index = index + 1,
                original = result.originalSentence,
                phonetics = result.phonetics,
                recognized = result.recognizedSentence,
                analysis = result.analysis,
                mistakes = result.mispronouncedPhonemes.joinToString(", ")
            )
        }

        /*
        val mistakes = listOf(
            SpeakingMistake(1, "What is the capital of Australia?", "/wɒt ɪz ðə ˈkæpɪtəl əv ɒˈstreɪliə/", "What is the capital of Austria", "Australia"),
            SpeakingMistake(2, "Which planet is closest to the Sun?", "/wɪtʃ ˈplænɪt ɪz ˈkləʊsɪst tə ðə sʌn/", "Which planet is closest to the Soon", "Sun"),
            SpeakingMistake(3, "How can I improve my coding skills?", "/haʊ kæn aɪ ɪmˈpruːv maɪ ˈkəʊdɪŋ skɪlz/", "How can I improof my codding skils", "improve, coding, skills"),
            SpeakingMistake(4, "Practice makes perfect.", "/ˈpræktɪs meɪks ˈpɜːrfɪkt/", "Practise makes perfict", "Practice, perfect"),
            SpeakingMistake(5, "The weather is beautiful today.", "/ðə ˈweðər ɪz ˈbjuːtɪfəl təˈdeɪ/", "The wether is beatiful today", "weather, beautiful"),
            SpeakingMistake(6, "Could you please repeat that?", "/kʊd juː pliːz rɪˈpiːt ðæt/", "Could you pleze repeat that", "please"),
            SpeakingMistake(7, "I am learning Android development.", "/aɪ æm ˈlɜːrnɪŋ ˈændrɔɪd dɪˈveləpmənt/", "I am lerning Android developement", "learning, development"),
            SpeakingMistake(8, "Kotlin is a modern language.", "/ˈkɒtlɪn ɪz ə ˈmɒdərn ˈlæŋɡwɪdʒ/", "Kotlin is a moder language", "modern"),
            SpeakingMistake(9, "Success requires hard work.", "/səkˈses rɪˈkwaɪərz hɑːrd wɜːrk/", "Sucess requires hard work", "Success"),
            SpeakingMistake(10, "Don't give up on your dreams.", "/dəʊnt ɡɪv ʌp ɒn jɔːr driːmz/", "Dont give up on your dreams", "Don't")
        )
        */

        binding.rvMistakes.layoutManager = LinearLayoutManager(this)
        binding.rvMistakes.adapter = SpeakingMistakeAdapter(mistakes)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnFinish.setOnClickListener { 
            // Xóa sạch session khi hoàn thành
            SpeakingSessionManager.clearSession()
            finish() 
        }
    }
}
