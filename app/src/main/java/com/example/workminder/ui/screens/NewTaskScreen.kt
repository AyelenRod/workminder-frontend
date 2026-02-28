package com.example.workminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workminder.data.model.MockData
import com.example.workminder.ui.components.WorkMinderTopBar
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(navController: NavController) {
    var taskName    by remember { mutableStateOf("") }
    var subject     by remember { mutableStateOf("") }
    var dueDate     by remember { mutableStateOf("") }
    var importance  by remember { mutableStateOf("") }
    var complexity  by remember { mutableStateOf("") }
    var notes       by remember { mutableStateOf("") }
    val subtasks    = remember { mutableStateListOf("") }

    var subjectExpanded    by remember { mutableStateOf(false) }
    var importanceExpanded by remember { mutableStateOf(false) }
    var complexityExpanded by remember { mutableStateOf(false) }

    val subjects    = listOf("Proyecto Integrador 2", "Cálculo Diferencial", "Física", "Química", "Sistemas Operativos")
    val importances = listOf("Muy urgente", "Algo urgente", "Muy poco urgente")
    val complexities= listOf("Alta", "Media", "Baja")

    Scaffold(
        topBar = {
            WorkMinderTopBar(
                subtitle = "La Agenda de", 
                name = MockData.userName,
                onSettingsClick = { navController.navigate(NavRoutes.Settings.route) }
            )
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Back + title
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = NavyText)
                }
                Text(
                    text = "Nueva tarea",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Task name
            FormLabel("Nombre de la tarea")
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                placeholder = { Text("Placeholder de prueba", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = fieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subject dropdown
            FormLabel("Materia")
            ExposedDropdownMenuBox(expanded = subjectExpanded, onExpandedChange = { subjectExpanded = it }) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecciona una opción", color = TextSecondary) },
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors()
                )
                ExposedDropdownMenu(expanded = subjectExpanded, onDismissRequest = { subjectExpanded = false }) {
                    subjects.forEach { opt ->
                        DropdownMenuItem(text = { Text(opt) }, onClick = { subject = opt; subjectExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Due date
            FormLabel("Fecha de entrega")
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                placeholder = { Text("dd/mm/aaaa", color = TextSecondary) },
                trailingIcon = {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = TextSecondary)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = fieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Importance dropdown
            FormLabel("Importancia")
            ExposedDropdownMenuBox(expanded = importanceExpanded, onExpandedChange = { importanceExpanded = it }) {
                OutlinedTextField(
                    value = importance,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecciona una opción", color = TextSecondary) },
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors()
                )
                ExposedDropdownMenu(expanded = importanceExpanded, onDismissRequest = { importanceExpanded = false }) {
                    importances.forEach { opt ->
                        DropdownMenuItem(text = { Text(opt) }, onClick = { importance = opt; importanceExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Complexity dropdown
            FormLabel("Complejidad")
            ExposedDropdownMenuBox(expanded = complexityExpanded, onExpandedChange = { complexityExpanded = it }) {
                OutlinedTextField(
                    value = complexity,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Selecciona una opción", color = TextSecondary) },
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors()
                )
                ExposedDropdownMenu(expanded = complexityExpanded, onDismissRequest = { complexityExpanded = false }) {
                    complexities.forEach { opt ->
                        DropdownMenuItem(text = { Text(opt) }, onClick = { complexity = opt; complexityExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notes
            FormLabel("Notas extra")
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("Opcional", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(8.dp),
                maxLines = 4,
                colors = fieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Subtasks
            FormLabel("Subtareas")
            subtasks.forEachIndexed { index, subtask ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = subtask,
                        onValueChange = { subtasks[index] = it },
                        placeholder = { Text("Nombre de la subtarea", color = TextSecondary) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        colors = fieldColors()
                    )
                    if (subtasks.size > 1) {
                        IconButton(onClick = { subtasks.removeAt(index) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            OutlinedButton(
                onClick = { subtasks.add("") },
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NavyText)
            ) {
                Text("+", color = NavyText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaveGreen, contentColor = Color.White)
            ) {
                Text("Guardar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = NavyText,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = YellowPrimary,
    unfocusedBorderColor = NavyText.copy(alpha = 0.35f),
    focusedContainerColor   = Color.White,
    unfocusedContainerColor = Color.White
)
