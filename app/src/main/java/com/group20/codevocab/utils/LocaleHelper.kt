package com.group20.codevocab.utils

import android.content.Context
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.*

object LocaleHelper {

    // Lưu lựa chọn ngôn ngữ vào SharedPreferences
    fun saveLanguage(context: Context, languageCode: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putString("app_language", languageCode).apply()
    }

    // Lấy ngôn ngữ hiện tại
    fun getLanguage(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString("app_language", "en") ?: "en"
    }

    // Áp dụng ngôn ngữ vào Context
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        return context.createConfigurationContext(config)
    }
}
