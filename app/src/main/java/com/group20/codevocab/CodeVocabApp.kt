package com.group20.codevocab

import android.app.Application
import com.group20.codevocab.utils.UserSessionManager

class CodeVocabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize UserSessionManager once here
        UserSessionManager.init(this)
    }
}
