package com.example.workminder.data.remote

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREFS_NAME = "workminder_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_PUSH_ENABLED = "push_enabled"
    private const val KEY_EMAIL_ENABLED = "email_enabled"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        token = prefs.getString(KEY_TOKEN, null)
        userId = prefs.getString(KEY_USER_ID, null)
        pushNotificationsEnabled = prefs.getBoolean(KEY_PUSH_ENABLED, true)
        emailNotificationsEnabled = prefs.getBoolean(KEY_EMAIL_ENABLED, false)
    }

    var pushNotificationsEnabled: Boolean = true
        set(value) {
            field = value
            prefs.edit().putBoolean(KEY_PUSH_ENABLED, value).apply()
        }

    var emailNotificationsEnabled: Boolean = false
        set(value) {
            field = value
            prefs.edit().putBoolean(KEY_EMAIL_ENABLED, value).apply()
        }

    var token: String? = null
        set(value) {
            field = value
            prefs.edit().putString(KEY_TOKEN, value).apply()
        }

    var userId: String? = null
        set(value) {
            field = value
            prefs.edit().putString(KEY_USER_ID, value).apply()
        }

    fun isLoggedIn(): Boolean = token != null

    fun clear() {
        token = null
        userId = null
        prefs.edit().clear().apply()
    }
}
