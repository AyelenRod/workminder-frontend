package com.example.workminder.data.remote

object AuthManager {
    var token: String? = null
    var userId: String? = null
    
    fun isLogged() = token != null
    
    fun logout() {
        token = null
        userId = null
    }
}
