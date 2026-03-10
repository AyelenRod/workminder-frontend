package com.example.workminder.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import com.example.workminder.data.local.AppDatabase
import com.example.workminder.data.model.User
import com.example.workminder.data.repository.AuthRepository
import com.example.workminder.data.remote.AuthManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository()
    private val userDao = AppDatabase.getDatabase(application).userDao()

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    fun register(firstName: String, lastName: String, email: String, pass: String, confirmPass: String) {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || pass.isBlank()) {
            errorMessage = "Por favor completa todos los campos"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Ingresa un correo electrónico válido"
            return
        }
        if (pass.length < 8) {
            errorMessage = "La contraseña debe tener al menos 8 caracteres"
            return
        }
        if (pass.contains(" ")) {
            errorMessage = "La contraseña no debe contener espacios"
            return
        }
        if (pass != confirmPass) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response = repository.register(email, pass, firstName.trim(), lastName.trim())

                if (response.isSuccessful && response.body()?.success == true) {
                    val auth = response.body()?.data
                    AuthManager.token = auth?.accessToken
                    AuthManager.userId = auth?.user?.id
                    
                    // Guardar usuario localmente
                    auth?.user?.let { remoteUser ->
                        userDao.insertUser(User(
                            id = remoteUser.id,
                            firstName = remoteUser.firstName ?: "",
                            lastName = remoteUser.lastName ?: "",
                            email = remoteUser.email
                        ))
                    }
                    
                    isSuccess = true
                } else {
                    errorMessage = response.body()?.error ?: "Error al registrarse"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val res = repository.login(email, pass)
                if (res.isSuccessful && res.body()?.success == true) {
                    val auth = res.body()?.data
                    AuthManager.token = auth?.accessToken
                    AuthManager.userId = auth?.user?.id
                    
                    // Guardar usuario localmente
                    auth?.user?.let { remoteUser ->
                        userDao.insertUser(User(
                            id = remoteUser.id,
                            firstName = remoteUser.firstName ?: "",
                            lastName = remoteUser.lastName ?: "",
                            email = remoteUser.email
                        ))
                    }
                    
                    isSuccess = true
                } else {
                    errorMessage = res.body()?.error ?: "Error en login"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun changePassword(newPassword: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val data = mapOf("new_password" to newPassword)
                val response = repository.changePassword(data)
                if (response.isSuccessful && response.body()?.success == true) {
                    onResult(true, null)
                } else {
                    errorMessage = response.body()?.error ?: "Error al cambiar contraseña"
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                errorMessage = e.message
                onResult(false, errorMessage)
            } finally {
                isLoading = false
            }
        }
    }
}
