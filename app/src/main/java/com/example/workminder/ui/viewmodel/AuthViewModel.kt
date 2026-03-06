package com.example.workminder.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workminder.data.repository.AuthRepository
import com.example.workminder.data.remote.AuthManager
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    fun register(name: String, email: String, pass: String, confirmPass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            errorMessage = "Por favor completa todos los campos"
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
                val parts = name.trim().split(" ")
                val first = parts.getOrNull(0) ?: ""
                val last = parts.drop(1).joinToString(" ")

                val response = repository.register(email, pass, first, last)

                if (response.isSuccessful && response.body()?.success == true) {
                    val auth = response.body()?.data
                    AuthManager.token = auth?.accessToken
                    AuthManager.userId = auth?.user?.id
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
}
