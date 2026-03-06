package com.example.workminder.data.repository

import com.example.workminder.data.model.Subject
import com.example.workminder.data.remote.RetrofitClient
import com.example.workminder.data.remote.ApiResponse
import retrofit2.Response

class SubjectRepository {
    private val api = RetrofitClient.apiService

    suspend fun getSubjects(): Response<ApiResponse<List<Subject>>> = api.getSubjects()

    suspend fun createSubject(name: String, color: String): Response<ApiResponse<Subject>> {
        return api.createSubject(mapOf("subject_name" to name, "color" to color))
    }

    suspend fun updateSubject(id: String, name: String, color: String): Response<ApiResponse<Subject>> {
        return api.updateSubject(id, mapOf("subject_name" to name, "color" to color))
    }

    suspend fun deleteSubject(id: String): Response<ApiResponse<Unit>> = api.deleteSubject(id)
}
