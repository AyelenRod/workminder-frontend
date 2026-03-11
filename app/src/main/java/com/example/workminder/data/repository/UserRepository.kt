package com.example.workminder.data.repository

import com.example.workminder.data.local.UserDao
import com.example.workminder.data.model.User
import com.example.workminder.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao,
    private val apiService: ApiService
) {
    fun getUser(): Flow<User?> = userDao.getUser()

    suspend fun syncUserProfile() {
        try {
            val response = apiService.getUserProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    userDao.insertUser(it)
                }
            }
        } catch (e: Exception) {
            // Silently fail if network is down
        }
    }

    suspend fun updateProfile(firstName: String, lastName: String): Boolean {
        return try {
            val response = apiService.updateProfile(mapOf("first_name" to firstName, "last_name" to lastName))
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    userDao.insertUser(it)
                }
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearSession() {
        userDao.clearUser()
    }
}
