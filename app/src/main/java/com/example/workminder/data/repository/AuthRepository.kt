package com.example.workminder.data.repository

import com.example.workminder.data.remote.RetrofitClient
import com.example.workminder.data.remote.ApiResponse
import com.example.workminder.data.remote.AuthResponse
import retrofit2.Response

class AuthRepository {
    suspend fun register(email: String, password: String, firstName: String, lastName: String): Response<ApiResponse<AuthResponse>> {
        val data = mapOf(
            "email" to email,
            "password" to password,
            "first_name" to firstName,
            "last_name" to lastName
        )
        return RetrofitClient.apiService.register(data)
    }

    suspend fun login(email: String, password: String): Response<ApiResponse<AuthResponse>> {
        val credentials = mapOf("email" to email, "password" to password)
        return RetrofitClient.apiService.login(credentials)
    }

    suspend fun updateProfile(firstName: String, lastName: String): Response<ApiResponse<com.example.workminder.data.model.User>> {
        val data = mapOf("first_name" to firstName, "last_name" to lastName)
        return RetrofitClient.apiService.updateProfile(data)
    }

    suspend fun changePassword(data: Map<String, String>): Response<ApiResponse<Unit>> {
        return RetrofitClient.apiService.changePassword(data)
    }
}
