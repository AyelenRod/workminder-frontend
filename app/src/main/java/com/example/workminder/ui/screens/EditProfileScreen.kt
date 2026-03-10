package com.example.workminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workminder.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.workminder.ui.theme.BackgroundGray
import com.example.workminder.ui.theme.NavyText
import com.example.workminder.ui.theme.SaveGreen
import com.example.workminder.ui.theme.YellowPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val user = viewModel.currentUser
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            firstName = it.firstName
            lastName = it.lastName
            email = it.email
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
// ...
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Nombre",
                style = MaterialTheme.typography.titleMedium,
                color = NavyText,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = YellowPrimary,
                    unfocusedBorderColor = NavyText.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Apellido",
                style = MaterialTheme.typography.titleMedium,
                color = NavyText,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = YellowPrimary,
                    unfocusedBorderColor = NavyText.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Correo Electrónico (No modificable)",
                style = MaterialTheme.typography.titleMedium,
                color = NavyText.copy(alpha = 0.6f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        if (firstName.isBlank() || lastName.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Completa todos los campos") }
                        } else {
                            viewModel.updateProfile(firstName, lastName) { success, error ->
                                if (success) {
                                    scope.launch { snackbarHostState.showSnackbar("Perfil actualizado") }
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar(error ?: "Error") }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaveGreen)
                ) {
                    Text("Guardar Cambios", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
