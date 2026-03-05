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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(navController: NavController) {

    var currentPassword    by remember { mutableStateOf("") }
    var newPassword        by remember { mutableStateOf("") }
    var confirmPassword    by remember { mutableStateOf("") }

    var showCurrent  by remember { mutableStateOf(false) }
    var showNew      by remember { mutableStateOf(false) }
    var showConfirm  by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

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
                "Cambiar Contraseña",
                style = MaterialTheme.typography.titleMedium,
                color = NavyText,
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
                onValueChange = { currentPassword = it; errorMessage = "" },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("••••••••", color = NavyText.copy(alpha = 0.4f)) },
                visualTransformation = if (showCurrent) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showCurrent = !showCurrent }) {
                        Icon(
                            imageVector = if (showCurrent) Icons.Filled.VisibilityOff
                                          else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = NavyText.copy(alpha = 0.5f)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = YellowPrimary,
                    unfocusedBorderColor = NavyText.copy(alpha = 0.35f),
                    focusedTextColor     = NavyText,
                    unfocusedTextColor   = NavyText
                ),
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
                onValueChange = { newPassword = it; errorMessage = "" },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Mínimo 6 caracteres", color = NavyText.copy(alpha = 0.4f)) },
                visualTransformation = if (showNew) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showNew = !showNew }) {
                        Icon(
                            imageVector = if (showNew) Icons.Filled.VisibilityOff
                                          else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = NavyText.copy(alpha = 0.5f)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = YellowPrimary,
                    unfocusedBorderColor = NavyText.copy(alpha = 0.35f),
                    focusedTextColor     = NavyText,
                    unfocusedTextColor   = NavyText
                ),
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
                onValueChange = { confirmPassword = it; errorMessage = "" },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Repite la nueva contraseña", color = NavyText.copy(alpha = 0.4f)) },
                visualTransformation = if (showConfirm) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(
                            imageVector = if (showConfirm) Icons.Filled.VisibilityOff
                                          else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = NavyText.copy(alpha = 0.5f)
                        )
                    }
                },
                isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = YellowPrimary,
                    unfocusedBorderColor = NavyText.copy(alpha = 0.35f),
                    focusedTextColor     = NavyText,
                    unfocusedTextColor   = NavyText,
                    errorBorderColor     = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(8.dp)
            )

            if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Las contraseñas no coinciden",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    when {
                        currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() ->
                            errorMessage = "Completa todos los campos."
                        newPassword != confirmPassword ->
                            errorMessage = "Las contraseñas no coinciden."
                        newPassword.length < 6 ->
                            errorMessage = "La nueva contraseña debe tener al menos 6 caracteres."
                        else -> {
                            // TODO: conectar con Supabase Auth → supabase.auth.updateUser { password = newPassword }
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaveGreen)
            ) {
                Text("Actualizar Contraseña", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
