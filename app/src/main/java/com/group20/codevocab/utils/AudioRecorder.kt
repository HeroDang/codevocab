package com.group20.codevocab.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): File? {
        val outputDir = context.cacheDir
        outputFile = File.createTempFile("audio_record", ".mp3", outputDir)

        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) // MP4 container supports AAC, usually compatible
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC) // AAC is widely supported
            setOutputFile(outputFile?.absolutePath)
            
            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("AudioRecorder", "prepare() failed", e)
                return null
            }
        }
        return outputFile
    }

    fun stopRecording() {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) {
             Log.e("AudioRecorder", "stop() failed", e)
        }
        mediaRecorder = null
    }

    fun release() {
        mediaRecorder?.release()
        mediaRecorder = null
    }
}