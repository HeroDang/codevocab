package com.group20.codevocab.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.group20.codevocab.data.remote.dto.UserDto

class UserSessionManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
    private val gson = Gson()
    private var cachedUser: UserDto? = null

    companion object {
        private const val KEY_USER = "current_user"

        @Volatile
        private var instance: UserSessionManager? = null

        fun init(context: Context): UserSessionManager {
            return instance ?: synchronized(this) {
                instance ?: UserSessionManager(context.applicationContext).also { instance = it }
            }
        }

        fun getInstance(): UserSessionManager {
            return instance ?: throw IllegalStateException("UserSessionManager must be initialized in Application class")
        }
    }

    fun saveUser(user: UserDto) {
        cachedUser = user
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply()
    }

    fun getUser(): UserDto? {
        if (cachedUser == null) {
            val json = prefs.getString(KEY_USER, null)
            if (json != null) {
                try {
                    cachedUser = gson.fromJson(json, UserDto::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return cachedUser
    }

    fun getUserId(): String? {
        return getUser()?.id
    }

    fun clear() {
        cachedUser = null
        prefs.edit().remove(KEY_USER).apply()
    }
}
