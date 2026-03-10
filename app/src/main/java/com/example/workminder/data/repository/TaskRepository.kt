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
                val remoteTasks = response.body()?.data
                if (!remoteTasks.isNullOrEmpty()) {
                    // Solo actualizamos/insertamos lo que viene del servidor
                    // No borramos lo local a menos que estemos seguros
                    taskDao.insertTasks(remoteTasks)
                }
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
        }
    }

    suspend fun createTask(task: Task) {
        // Guardar localmente marcado como no sincronizado
        val localTask = task.copy(is_synced = false)
        taskDao.insertTask(localTask)
        
        try {
            val response = apiService.createTask(task)
            if (response.isSuccessful && response.body()?.success == true) {
                val remoteTask = response.body()?.data
                if (remoteTask != null) {
                    // Si el servidor devolvió un ID diferente o para asegurar consistencia
                    if (remoteTask.id != task.id) {
                        taskDao.deleteTaskById(task.id)
                    }
                    taskDao.insertTask(remoteTask.copy(is_synced = true))
                }
            } else {
                android.util.Log.e("TaskRepository", "Error creando tarea: ${response.code()} ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("TaskRepository", "Fallo de red creando tarea: ${e.message}")
        }
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
        try {
            val response = apiService.updateTask(task.id, task)
            if (!response.isSuccessful) {
                android.util.Log.e("TaskRepository", "Error actualizando tarea: ${response.code()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("TaskRepository", "Fallo de red al actualizar: ${e.message}")
        }
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTaskById(task.id)
        try {
            val response = apiService.deleteTask(task.id)
            if (!response.isSuccessful) {
                android.util.Log.e("TaskRepository", "Error eliminando tarea: ${response.code()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("TaskRepository", "Fallo de red al eliminar: ${e.message}")
        }
    }
}
