package com.group20.codevocab.utils

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("APP_STATS", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOTAL_TIME = "TOTAL_TIME_TODAY"
        private const val KEY_LAST_DATE = "LAST_DATE"
    }

    fun addStudyTime(minutes: Long) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        val lastDate = prefs.getString(KEY_LAST_DATE, "")

        if (today != lastDate) {
            // New day, reset time
            prefs.edit()
                .putLong(KEY_TOTAL_TIME, minutes)
                .putString(KEY_LAST_DATE, today)
                .apply()
        } else {
            // Same day, accumulate time
            val currentTotal = prefs.getLong(KEY_TOTAL_TIME, 0)
            prefs.edit().putLong(KEY_TOTAL_TIME, currentTotal + minutes).apply()
        }
    }

    fun getTodayStudyTime(): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        val lastDate = prefs.getString(KEY_LAST_DATE, "")

        return if (today == lastDate) {
            prefs.getLong(KEY_TOTAL_TIME, 0)
        } else {
            0
        }
    }
}
