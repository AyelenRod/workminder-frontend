package com.example.workminder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workminder.R
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.BackgroundGray
import com.example.workminder.ui.theme.NavyText
import com.example.workminder.ui.theme.TextSecondary
import com.example.workminder.ui.theme.YellowPrimary
import com.example.workminder.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPrivacyPolicy by remember { mutableStateOf(false) }

    // Observar éxito para navegar automáticamente
    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) {
            navController.navigate(NavRoutes.Dashboard.route) {
                popUpTo(NavRoutes.Login.route) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = BackgroundGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_workminder_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(90.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Crea una cuenta",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = NavyText
            )
            Text(
                "Empieza a organizar todo con WorkMinder",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Inputs del Formulario
            OutlinedTextField(
                value = firstName, onValueChange = { firstName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = YellowPrimary, unfocusedBorderColor = NavyText.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lastName, onValueChange = { lastName = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = YellowPrimary, unfocusedBorderColor = NavyText.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = YellowPrimary, unfocusedBorderColor = NavyText.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = YellowPrimary, unfocusedBorderColor = NavyText.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword, onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = YellowPrimary, unfocusedBorderColor = NavyText.copy(alpha = 0.3f))
            )

            // Manejo de Errores desde el ViewModel
            if (viewModel.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(viewModel.errorMessage!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Registro vinculado al ViewModel
            Button(
                onClick = { showPrivacyPolicy = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = NavyText),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NavyText, strokeWidth = 2.dp)
                } else {
                    Text("Registrarme", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text("¿Ya tienes cuenta? ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(
                    "Inicia sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyText,
                    modifier = Modifier.clickable { navController.popBackStack() }
                )
            }
        }

        if (showPrivacyPolicy) {
            AlertDialog(
                onDismissRequest = { showPrivacyPolicy = false },
                title = {
                    Text("Políticas de Privacidad", fontWeight = FontWeight.Bold, color = NavyText)
                },
                text = {
                    Text(
                        """
                            AVISO DE PRIVACIDAD (BETA ACADÉMICA)
                            
                            Responsabilidad y Compromiso:
                            Esta aplicación es un proyecto de carácter estrictamente académico. Nos comprometemos a resguardar tus datos personales y de uso bajo estándares de responsabilidad y ética, asegurando que no sean compartidos con terceros ajenos a este proyecto.
                            
                            Finalidad de los datos:
                            La información recopilada será utilizada exclusivamente para:
                            ${'\u2022'} Análisis académico. Evaluar el uso de los usuarios para fines de aprendizaje e investigación técnica.
                            ${'\u2022'} Mejora continua. Identificar errores y optimizar la experiencia de usuario durante esta etapa.
                            
                            Vigencia de uso:
                            El periodo para el uso de esta aplicación y el tratamiento de datos está limitado hasta el mes de Abril de 2026
                            
                            Consentimiento:
                            Al continuar y crear tu cuenta, aceptas nuestra Declaración de Políticas de Privacidad. ¿Deseas continuar con tu registro?
                        """.trimIndent(),
                        color = NavyText
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showPrivacyPolicy = false
                            viewModel.register(firstName, lastName, email, password, confirmPassword)
                        }
                    ) {
                        Text("Continuar con mi registro", color = NavyText, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showPrivacyPolicy = false }
                    ) {
                        Text("Rechazar", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = BackgroundGray,
                titleContentColor = NavyText,
                textContentColor = NavyText
            )
        }
    }
}
