package com.group20.codevocab.ui.common.speaker

import android.content.Context

object SpeakerFactory {

    fun create(context: Context): Speaker {
        return AndroidTtsSpeaker(context)
    }
}
