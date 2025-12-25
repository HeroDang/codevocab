package com.group20.codevocab.data.local

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AUTH_PREFS", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "ACCESS_TOKEN"
        private const val USER_ID_KEY = "USER_ID"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).remove(USER_ID_KEY).apply()
    }

    fun saveUserId(userId: String) {
        prefs.edit().putString(USER_ID_KEY, userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString(USER_ID_KEY, null)
    }
}
