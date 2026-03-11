package com.example.workminder.data.remote

import com.example.workminder.data.model.Subject
import com.example.workminder.data.model.Task
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<ApiResponse<AuthResponse>>

    @POST("auth/register")
    suspend fun register(@Body data: Map<String, String>): Response<ApiResponse<AuthResponse>>

    @GET("auth/me")
    suspend fun getUserProfile(): Response<ApiResponse<com.example.workminder.data.model.User>>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body data: Map<String, String>): Response<ApiResponse<com.example.workminder.data.model.User>>

    @PUT("auth/password")
    suspend fun changePassword(@Body data: Map<String, String>): Response<ApiResponse<Unit>>

    // Tasks
    @GET("tasks")
    suspend fun getTasks(): Response<ApiResponse<List<Task>>>

    @GET("tasks/prioritized")
    suspend fun getPrioritizedTasks(): Response<ApiResponse<List<Task>>>

    @POST("tasks")
    suspend fun createTask(@Body task: Task): Response<ApiResponse<Task>>

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: Task): Response<ApiResponse<Task>>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<ApiResponse<Unit>>

    // Subjects
    @GET("subjects")
    suspend fun getSubjects(): Response<ApiResponse<List<Subject>>>

    @POST("subjects")
    suspend fun createSubject(@Body data: Map<String, String>): Response<ApiResponse<Subject>>

    @PUT("subjects/{id}")
    suspend fun updateSubject(@Path("id") id: String, @Body data: Map<String, String>): Response<ApiResponse<Subject>>

    @DELETE("subjects/{id}")
    suspend fun deleteSubject(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("tasks/{id}/reminders")
    suspend fun addReminder(@Path("id") id: String, @Body reminder: com.example.workminder.data.model.Reminder): Response<ApiResponse<com.example.workminder.data.model.Reminder>>
}
