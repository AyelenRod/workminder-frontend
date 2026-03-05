package com.example.workminder.data.remote

import com.example.workminder.data.model.Task
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("api/tasks")
    suspend fun getTasks(): Response<List<Task>>

    @POST("api/tasks")
    suspend fun createTask(@Body task: Task): Response<Task>

    @PUT("api/tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: Task): Response<Task>

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<Unit>
}
