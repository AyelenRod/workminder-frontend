package com.example.workminder.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workminder.data.model.Subject
import com.example.workminder.data.model.Task
import com.example.workminder.data.model.User
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
    private val subjectDao = AppDatabase.getDatabase(application).subjectDao()
    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val authRepo = com.example.workminder.data.repository.AuthRepository()
    private val taskRepo = TaskRepository(taskDao, RetrofitClient.apiService)
    private val subjectRepo = SubjectRepository(subjectDao, RetrofitClient.apiService)
    private val scheduler = com.example.workminder.notifications.ReminderScheduler(application)

    // Estados
    var tasks = mutableStateListOf<Task>()
    var subjects = mutableStateListOf<Subject>()
    var currentUser by mutableStateOf<User?>(null)
    var userName by mutableStateOf("Usuario")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            userDao.getUser().collect { user ->
                currentUser = user
                user?.let {
                    userName = it.firstName
                }
            }
        }
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
        viewModelScope.launch {
            subjectRepo.getAllSubjects().collect { list ->
                subjects.clear()
                subjects.addAll(list)
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
                // Asegurar que tenemos el IP del servidor antes de sincronizar
                com.example.workminder.data.remote.NetworkConfig.discoverServer(getApplication())
                
                taskRepo.syncTasks()
                subjectRepo.syncSubjects()
                
                // Sincronizar usuario
                val userRes = RetrofitClient.apiService.getUserProfile()
                if (userRes.isSuccessful && userRes.body()?.success == true) {
                    userRes.body()?.data?.let { userDao.insertUser(it) }
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

    // --- Perfil ---
    fun updateProfile(firstName: String, lastName: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = authRepo.updateProfile(firstName, lastName)
                if (response.isSuccessful && response.body()?.success == true) {
                    val updatedUser = response.body()?.data
                    if (updatedUser != null) {
                        userDao.insertUser(updatedUser)
                        onResult(true, null)
                    } else {
                        onResult(false, "Respuesta vacía del servidor")
                    }
                } else {
                    onResult(false, response.body()?.error ?: "Error al actualizar perfil")
                }
            } catch (e: Exception) {
                onResult(false, e.message)
            } finally {
                isLoading = false
            }
        }
    }

    // --- Materias ---
    fun addSubject(name: String, color: String) {
        viewModelScope.launch {
            try {
                val newSubject = Subject(java.util.UUID.randomUUID().toString(), name, color)
                subjectRepo.createSubject(newSubject)
                refreshAll()
            } catch (e: Exception) { error = e.message }
        }
    }

    fun updateSubject(subject: Subject) {
        viewModelScope.launch {
            try {
                subjectRepo.updateSubject(subject)
                refreshAll()
            } catch (e: Exception) { error = e.message }
        }
    }

    fun deleteSubject(id: String) {
        viewModelScope.launch {
            try {
                subjectRepo.deleteSubject(id)
                // Evitar huerfanos: nular dependencias
                tasks.filter { it.subject_id == id }.forEach { orphanedTask ->
                    updateTask(orphanedTask.copy(subject_id = null))
                }
                refreshAll()
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
