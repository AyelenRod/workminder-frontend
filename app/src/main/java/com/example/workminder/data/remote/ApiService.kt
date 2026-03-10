package com.example.workminder.data.remote

import com.example.workminder.data.model.Subject
import com.example.workminder.data.model.Task
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<ApiResponse<AuthResponse>>

    @POST("api/auth/register")
    suspend fun register(@Body data: Map<String, String>): Response<ApiResponse<AuthResponse>>

    @GET("api/auth/me")
    suspend fun getUserProfile(): Response<ApiResponse<com.example.workminder.data.model.User>>

    @PUT("api/auth/profile")
    suspend fun updateProfile(@Body data: Map<String, String>): Response<ApiResponse<com.example.workminder.data.model.User>>

    @PUT("api/auth/password")
    suspend fun changePassword(@Body data: Map<String, String>): Response<ApiResponse<Unit>>

    // Tasks
    @GET("api/tasks")
    suspend fun getTasks(): Response<ApiResponse<List<Task>>>

    @GET("api/tasks/prioritized")
    suspend fun getPrioritizedTasks(): Response<ApiResponse<List<Task>>>

    @POST("api/tasks")
    suspend fun createTask(@Body task: Task): Response<ApiResponse<Task>>

    @PUT("api/tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: Task): Response<ApiResponse<Task>>

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<ApiResponse<Unit>>

    // Subjects
    @GET("api/subjects")
    suspend fun getSubjects(): Response<ApiResponse<List<Subject>>>

    @POST("api/subjects")
    suspend fun createSubject(@Body data: Map<String, String>): Response<ApiResponse<Subject>>

    @PUT("api/subjects/{id}")
    suspend fun updateSubject(@Path("id") id: String, @Body data: Map<String, String>): Response<ApiResponse<Subject>>

    @DELETE("api/subjects/{id}")
    suspend fun deleteSubject(@Path("id") id: String): Response<ApiResponse<Unit>>
}
