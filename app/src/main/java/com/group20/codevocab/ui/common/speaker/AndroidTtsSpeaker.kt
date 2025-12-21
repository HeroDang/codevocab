package com.group20.codevocab.ui.common.speaker

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AndroidTtsSpeaker(
    context: Context,
    locale: Locale = Locale.US
) : Speaker {

    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(locale)
                ready = result != TextToSpeech.LANG_MISSING_DATA &&
                        result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    override fun speak(text: String?) {
        val word = text?.trim().orEmpty()
        if (word.isBlank() || !ready) return

        tts?.speak(
            word,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "tts_$word"
        )
    }

    override fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
