package com.example.workminder.data.repository

import com.example.workminder.data.local.TaskDao
import com.example.workminder.data.model.Task
import com.example.workminder.data.remote.ApiService
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val apiService: ApiService
) {
    fun getTasks(userId: String): Flow<List<Task>> {
        return taskDao.getTasksByUser(userId)
    }

    suspend fun syncTasks() {
        try {
            val response = apiService.getTasks()
            if (response.isSuccessful && response.body()?.success == true) {
                val remoteTasks = response.body()?.data
                if (!remoteTasks.isNullOrEmpty()) {
                    taskDao.insertTasks(remoteTasks)
                }
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        }
    }

    suspend fun createTask(task: Task) {
        val localTask = task.copy(is_synced = false)
        taskDao.insertTask(localTask)
        
        try {
            val response = apiService.createTask(task)
            if (response.isSuccessful && response.body()?.success == true) {
                val remoteTask = response.body()?.data
                if (remoteTask != null) {
                    if (remoteTask.id != task.id) {
                        taskDao.deleteTaskById(task.id)
                    }
                    taskDao.insertTask(remoteTask.copy(is_synced = true))
                }
            }
        } catch (e: Exception) {}
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
        try {
            apiService.updateTask(task.id, task)
        } catch (e: Exception) {}
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTaskById(task.id)
        try {
            apiService.deleteTask(task.id)
        } catch (e: Exception) {}
    }

    suspend fun addReminder(taskId: String, reminder: com.example.workminder.data.model.Reminder) {
        try {
            apiService.addReminder(taskId, reminder)
        } catch (e: Exception) {}
    }
}
