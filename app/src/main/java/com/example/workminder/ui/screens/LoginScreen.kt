package com.example.workminder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.BackgroundGray
import com.example.workminder.ui.theme.NavyText
import com.example.workminder.ui.theme.TextSecondary
import com.example.workminder.ui.theme.YellowPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(56.dp).offset(x = 15.dp, y = 5.dp).clip(CircleShape).background(NavyText))
                Icon(Icons.Filled.Edit, contentDescription = null, tint = YellowPrimary, modifier = Modifier.size(54.dp).offset(x = (-12).dp, y = 3.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Inicia sesión", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = NavyText)
            Spacer(modifier = Modifier.height(8.dp))
            Text("¡Bienvenido de nuevo a WorkMinder!", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            
            Spacer(modifier = Modifier.height(40.dp))

            // Form
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

            Spacer(modifier = Modifier.height(32.dp))

            // Button
            Button(
                onClick = {
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = NavyText)
            ) {
                Text("Entrar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register text
            Row(horizontalArrangement = Arrangement.Center) {
                Text("¿No tienes cuenta? ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(
                    "Regístrate",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyText,
                    modifier = Modifier.clickable { navController.navigate(NavRoutes.Register.route) }
                )
            }
        }
    }
}
