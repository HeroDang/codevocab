package com.group20.codevocab.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.group20.codevocab.data.remote.PronunciationApiClient
import com.group20.codevocab.data.repository.PronunciationCheckRepository
import com.group20.codevocab.utils.AudioRecorder

class PronunciationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PronunciationViewModel::class.java)) {
            val api = PronunciationApiClient.api
            val repository = PronunciationCheckRepository(api)
            val recorder = AudioRecorder(context)
            return PronunciationViewModel(repository, recorder) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
