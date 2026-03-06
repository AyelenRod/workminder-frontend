package com.example.workminder.data.repository

import com.example.workminder.data.local.TaskDao
import com.example.workminder.data.model.Task
import com.example.workminder.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val apiService: ApiService
) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun syncTasks() {
        try {
            val response = apiService.getTasks()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { remoteTasks ->
                    taskDao.insertTasks(remoteTasks)
                }
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        }
    }

    suspend fun createTask(task: Task) {
        taskDao.insertTask(task)
        try {
            val response = apiService.createTask(task)
            if (response.isSuccessful && response.body()?.success == true) {
                val remoteTask = response.body()?.data
                if (remoteTask != null) {
                    if (remoteTask.id != task.id) {
                        taskDao.deleteTaskById(task.id)
                    }
                    taskDao.insertTask(remoteTask)
                }
            }
        } catch (e: Exception) {
        }
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
        try {
            apiService.updateTask(task.id, task)
        } catch (e: Exception) {
        }
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTaskById(task.id)
        try {
            apiService.deleteTask(task.id)
        } catch (e: Exception) {
        }
    }
}
