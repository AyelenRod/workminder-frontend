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
    // Retorna flujo de la BD local
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    // Sincroniza de remoto a local
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
            // Otros errores de red o parsing
        }
    }

    suspend fun createTask(task: Task) {
        // Guardar local primero (Offline-First)
        taskDao.insertTask(task)
        try {
            // Intentar enviar al backend
            val response = apiService.createTask(task)
            if (response.isSuccessful && response.body()?.success == true) {
                // Actualizar con datos del server si es necesario (ej. IDs generados)
                response.body()?.data?.let { taskDao.insertTask(it) }
            }
        } catch (e: Exception) {
            // Si falla, se queda en BD local para sincronizar luego
        }
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
        try {
            apiService.updateTask(task.id, task)
        } catch (e: Exception) {
            // Pendiente de sincronización
        }
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTaskById(task.id)
        try {
            apiService.deleteTask(task.id)
        } catch (e: Exception) {
            // Pendiente de sincronización
        }
    }
}
