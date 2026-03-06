package com.example.workminder.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workminder.data.model.Subject
import com.example.workminder.data.model.Task
import com.example.workminder.data.repository.SubjectRepository
import com.example.workminder.data.repository.TaskRepository
import com.example.workminder.data.local.AppDatabase
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.workminder.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = AppDatabase.getDatabase(application).taskDao()
    private val taskRepo = TaskRepository(taskDao, RetrofitClient.apiService)
    private val subjectRepo = SubjectRepository()
    private val scheduler = com.example.workminder.notifications.ReminderScheduler(application)

    // Estados
    var tasks = mutableStateListOf<Task>()
    var subjects = mutableStateListOf<Subject>()
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            taskRepo.getAllTasks().collect { list ->
                tasks.clear()
                tasks.addAll(list)
            }
        }
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            isLoading = true
            try {
                taskRepo.syncTasks()

                val sRes = subjectRepo.getSubjects()
                if (sRes.isSuccessful) {
                    subjects.clear()
                    sRes.body()?.data?.let { subjects.addAll(it) }
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    error = e.message
                    println("Error en refreshAll: ${e.message}")
                }
            } finally {
                isLoading = false
            }
        }
    }

    // --- Materias ---
    fun addSubject(name: String, color: String) {
        viewModelScope.launch {
            try {
                val res = subjectRepo.createSubject(name, color)
                if (res.isSuccessful) refreshAll()
            } catch (e: Exception) { error = e.message }
        }
    }

    fun deleteSubject(id: String) {
        viewModelScope.launch {
            try {
                val res = subjectRepo.deleteSubject(id)
                if (res.isSuccessful) refreshAll()
            } catch (e: Exception) { error = e.message }
        }
    }

    // --- Tareas ---
    fun createTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepo.createTask(task)
                scheduler.schedule(task.id, task.title, task.due_date, task.reminders)
                refreshAll()
            } catch (e: Exception) { error = e.message }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepo.updateTask(task)
                scheduler.cancelAll(task.id)
                scheduler.schedule(task.id, task.title, task.due_date, task.reminders)
                refreshAll()
            } catch (e: Exception) { error = e.message }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepo.deleteTask(task)
                scheduler.cancelAll(task.id)
                refreshAll()
            } catch (e: Exception) { error = e.message }
        }
    }
}
