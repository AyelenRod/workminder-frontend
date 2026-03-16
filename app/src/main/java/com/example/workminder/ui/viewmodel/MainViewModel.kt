package com.example.workminder.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.example.workminder.data.model.Subject
import com.example.workminder.data.model.Task
import com.example.workminder.data.model.User
import com.example.workminder.data.repository.*
import com.example.workminder.data.local.AppDatabase
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.workminder.data.remote.RetrofitClient
import com.example.workminder.data.remote.NetworkConfig
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val taskRepo = TaskRepository(db.taskDao(), RetrofitClient.apiService)
    private val subjectRepo = SubjectRepository(db.subjectDao(), RetrofitClient.apiService)
    private val userRepo = UserRepository(db.userDao(), RetrofitClient.apiService)
    private val authRepo = AuthRepository()
    private val scheduler = com.example.workminder.notifications.ReminderScheduler(application)

    var tasks = mutableStateListOf<Task>()
    var subjects = mutableStateListOf<Subject>()
    var currentUser by mutableStateOf<User?>(null)
    var userName by mutableStateOf("Usuario")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        observeUserData()
        observeTasksData()
        observeSubjectsData()
        refreshAll()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            userRepo.getUser().collect { user ->
                currentUser = user
                user?.let { userName = it.firstName }
            }
        }
    }

    private fun observeTasksData() {
        viewModelScope.launch {
            taskRepo.getAllTasks().collect { list ->
                val today = LocalDate.now()
                val validTasks = mutableListOf<Task>()
                
                for (task in list) {
                    var shouldDelete = false
                    var shouldUpdate = false
                    
                    if (task.status == com.example.workminder.data.model.TaskStatus.DONE && task.completed_at != null) {
                        try {
                            val compDate = LocalDateTime.parse(task.completed_at, DateTimeFormatter.ISO_DATE_TIME).toLocalDate()
                            if (ChronoUnit.DAYS.between(compDate, today) > 7) shouldDelete = true
                        } catch(e: Exception) {}
                    } else if (task.status == com.example.workminder.data.model.TaskStatus.PENDING) {
                        try {
                            val due = LocalDate.parse(task.due_date.split("T")[0], DateTimeFormatter.ISO_LOCAL_DATE)
                            if (due.isBefore(today)) shouldUpdate = true
                        } catch(e: Exception) {}
                    }
                    
                    if (shouldDelete) {
                        deleteTask(task)
                    } else if (shouldUpdate) {
                        val updated = task.copy(status = com.example.workminder.data.model.TaskStatus.LATE)
                        updateTask(updated)
                        validTasks.add(updated)
                    } else {
                        validTasks.add(task)
                    }
                }
                tasks.clear()
                tasks.addAll(validTasks)
            }
        }
    }

    private fun observeSubjectsData() {
        viewModelScope.launch {
            subjectRepo.getAllSubjects().collect { list ->
                subjects.clear()
                subjects.addAll(list)
            }
        }
    }

    private var refreshJob: kotlinx.coroutines.Job? = null

    fun refreshAll() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            isLoading = true
            try {
                NetworkConfig.discoverServer(getApplication())
                taskRepo.syncTasks()
                subjectRepo.syncSubjects()
                userRepo.syncUserProfile()
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val cm = getApplication<Application>().getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun updateProfile(firstName: String, lastName: String, onResult: (Boolean, String?) -> Unit) {
        if (!isInternetAvailable()) {
            onResult(false, "Se requiere conexión a internet.")
            return
        }

        viewModelScope.launch {
            isLoading = true
            val success = userRepo.updateProfile(firstName, lastName)
            if (success) {
                userName = firstName
                onResult(true, null)
            } else {
                onResult(false, "Error al actualizar perfil")
            }
            isLoading = false
        }
    }

    fun addSubject(name: String, color: String) {
        viewModelScope.launch {
            try {
                subjectRepo.createSubject(Subject(java.util.UUID.randomUUID().toString(), name, color))
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
                tasks.filter { it.subject_id == id }.forEach { updateTask(it.copy(subject_id = null)) }
                refreshAll()
            } catch (e: Exception) { error = e.message }
        }
    }

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

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                db.clearAllTables()
            }
            userRepo.clearSession()
            com.example.workminder.data.remote.AuthManager.clear()
            userName = "Usuario"
            currentUser = null
            tasks.clear()
            subjects.clear()
            onComplete()
        }
    }
}
