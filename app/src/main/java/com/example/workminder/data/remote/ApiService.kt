package com.example.workminder.data.remote

import com.example.workminder.data.model.Task
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<ApiResponse<AuthResponse>>

    @POST("api/auth/register")
    suspend fun register(@Body data: Map<String, String>): Response<ApiResponse<AuthResponse>>

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
}
