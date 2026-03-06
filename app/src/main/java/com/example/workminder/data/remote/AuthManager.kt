package com.example.workminder.data.remote

object AuthManager {
    var token: String? = null
    var userId: String? = null

    fun clear() {
        token = null
        userId = null
    }
}
