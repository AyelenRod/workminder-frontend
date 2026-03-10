package com.example.workminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workminder.ui.theme.BackgroundGray
import com.example.workminder.ui.theme.NavyText
import com.example.workminder.ui.theme.SaveGreen
import com.example.workminder.ui.theme.YellowPrimary
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workminder.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {

    var currentPassword    by remember { mutableStateOf("") }
    var newPassword        by remember { mutableStateOf("") }
    var confirmPassword    by remember { mutableStateOf("") }

    var showCurrent  by remember { mutableStateOf(false) }
    var showNew      by remember { mutableStateOf(false) }
    var showConfirm  by remember { mutableStateOf(false) }

    var feedbackMessage by remember { mutableStateOf("") }
    var isSuccess       by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacidad y Seguridad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundGray,
                    titleContentColor = NavyText,
                    navigationIconContentColor = NavyText
                )
            )
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                if (isSuccess) "¡Éxito!" else "Cambiar Contraseña",
                style = MaterialTheme.typography.titleMedium,
                color = if (isSuccess) SaveGreen else NavyText,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Contraseña actual
            Text(
                "Contraseña actual",
                style = MaterialTheme.typography.bodyMedium,
                color = NavyText.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it; feedbackMessage = "" },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("••••••••", color = NavyText.copy(alpha = 0.4f)) },
                visualTransformation = if (showCurrent) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showCurrent = !showCurrent }) {
                        Icon(imageVector = if (showCurrent) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null, tint = NavyText.copy(alpha = 0.5f))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = YellowPrimary, unfocusedBorderColor = NavyText.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nueva contraseña
            Text(
                "Nueva contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = NavyText.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it; feedbackMessage = "" },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Mínimo 8 caracteres", color = NavyText.copy(alpha = 0.4f)) },
                visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showNew = !showNew }) {
                        Icon(imageVector = if (showNew) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null, tint = NavyText.copy(alpha = 0.5f))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = YellowPrimary, unfocusedBorderColor = NavyText.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirmar nueva contraseña
            Text(
                "Confirmar nueva contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = NavyText.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; feedbackMessage = "" },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Repite la nueva contraseña", color = NavyText.copy(alpha = 0.4f)) },
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(imageVector = if (showConfirm) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = null, tint = NavyText.copy(alpha = 0.5f))
                    }
                },
                isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = YellowPrimary, unfocusedBorderColor = NavyText.copy(alpha = 0.35f), errorBorderColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(8.dp)
            )

            if (feedbackMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(feedbackMessage, color = if (isSuccess) SaveGreen else MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        when {
                            newPassword.length < 8 -> feedbackMessage = "La contraseña debe tener al menos 8 caracteres."
                            newPassword.contains(" ") -> feedbackMessage = "No se permiten espacios."
                            newPassword != confirmPassword -> feedbackMessage = "Las contraseñas no coinciden."
                            else -> {
                                viewModel.changePassword(newPassword) { success, error ->
                                    if (success) {
                                        isSuccess = true
                                        feedbackMessage = "Contraseña actualizada correctamente."
                                    } else {
                                        feedbackMessage = error ?: "Error al actualizar."
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaveGreen)
                ) {
                    Text("Actualizar Contraseña", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
