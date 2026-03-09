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
                val today = java.time.LocalDate.now()
                val validTasks = mutableListOf<Task>()
                
                for (task in list) {
                    var shouldDelete = false
                    var shouldUpdate = false
                    
                    if (task.status == com.example.workminder.data.model.TaskStatus.DONE && task.completed_at != null) {
                        try {
                            val compDate = java.time.LocalDateTime.parse(task.completed_at, java.time.format.DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
                            if (java.time.temporal.ChronoUnit.DAYS.between(compDate, today) > 7) {
                                shouldDelete = true
                            }
                        } catch(e: Exception) {}
                    } else if (task.status == com.example.workminder.data.model.TaskStatus.PENDING) {
                        try {
                            val due = java.time.LocalDate.parse(task.due_date.split("T")[0], java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
                            if (due.isBefore(today)) {
                                shouldUpdate = true
                            }
                        } catch(e: Exception) {}
                    }
                    
                    if (shouldDelete) {
                        deleteTask(task)
                    } else if (shouldUpdate) {
                        updateTask(task.copy(status = com.example.workminder.data.model.TaskStatus.LATE))
                        validTasks.add(task.copy(status = com.example.workminder.data.model.TaskStatus.LATE))
                    } else {
                        validTasks.add(task)
                    }
                }
                
                tasks.clear()
                tasks.addAll(validTasks)
            }
        }
        refreshAll()
    }

    private var refreshJob: kotlinx.coroutines.Job? = null

    fun refreshAll() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
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
