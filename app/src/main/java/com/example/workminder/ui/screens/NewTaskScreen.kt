package com.example.workminder.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workminder.ui.viewmodel.MainViewModel
import com.example.workminder.data.model.Subtask
import com.example.workminder.ui.components.WorkMinderTopBar
import com.example.workminder.ui.components.WorkMinderDialog
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewTaskScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    var taskName    by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf("") }
    var selectedSubjectName by remember { mutableStateOf("") }
    var dueDate     by remember { mutableStateOf("") }
    var importance  by remember { mutableStateOf("") }
    var complexity  by remember { mutableStateOf("") }
    var notes       by remember { mutableStateOf("") }
    val subtasks    = remember { mutableStateListOf(java.util.UUID.randomUUID().toString() to "") }
    val selectedReminders = remember { mutableStateListOf<Int>() }

    var importanceExpanded by remember { mutableStateOf(false) }
    var complexityExpanded by remember { mutableStateOf(false) }
    var subjectExpanded by remember { mutableStateOf(false) }

    var showValidationError by remember { mutableStateOf(false) }

    val importances = listOf("Muy urgente", "Algo urgente", "Muy poco urgente")
    val complexities= listOf("Alta", "Media", "Baja")
    val reminderOptions = listOf(0, 1, 2, 3) // 0 es el día actual, 1 día antes, etc.
    val reminderLabels = mapOf(0 to "Hoy", 1 to "1 día antes", 2 to "2 días antes", 3 to "3 días antes")


    Scaffold(
        topBar = {
            WorkMinderTopBar(
                subtitle = "La Agenda de", 
                name = "Usuario",
                onSettingsClick = { navController.navigate(NavRoutes.Settings.route) }
            )
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        if (showValidationError) {
            WorkMinderDialog(
                onDismissRequest = { showValidationError = false },
                title = "Faltan datos",
                message = "Por favor, completa todos los campos obligatorios para crear la tarea.",
                confirmText = "Entendido",
                onConfirm = { showValidationError = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 64.dp)
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

            FormLabel("Nombre de la tarea")
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                placeholder = { Text("Escribe el nombre...", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = fieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

            FormLabel("Materia")
            ExposedDropdownMenuBox(expanded = subjectExpanded, onExpandedChange = { subjectExpanded = it }) {
                OutlinedTextField(
                    value = selectedSubjectName, onValueChange = {}, readOnly = true,
                    placeholder = { Text("Selecciona materia", color = TextSecondary) },
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, null, tint = NavyText) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(8.dp), colors = fieldColors()
                )
                ExposedDropdownMenu(expanded = subjectExpanded, onDismissRequest = { subjectExpanded = false }) {
                    viewModel.subjects.forEach { subj -> 
                        DropdownMenuItem(text = { Text(subj.subject_name) }, onClick = { 
                            selectedSubjectName = subj.subject_name
                            selectedSubjectId = subj.id
                            subjectExpanded = false 
                        }) 
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            var showDatePicker by remember { mutableStateOf(false) }
            if (showDatePicker) {
                val calendar = java.util.Calendar.getInstance()
                android.app.DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        // Formato YYYY-MM-DD
                        dueDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    },
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                ).show()
                showDatePicker = false
            }

            FormLabel("Fecha de entrega")
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                readOnly = true,
                placeholder = { Text("Selecciona una fecha", color = TextSecondary) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = TextSecondary)
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = fieldColors()
            )

            Spacer(modifier = Modifier.height(14.dp))

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

            FormLabel("Subtareas")
            subtasks.forEachIndexed { index, subtask ->
                key(subtask.first) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = subtask.second,
                            onValueChange = { subtasks[index] = subtask.first to it },
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
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Button(
                onClick = { subtasks.add(java.util.UUID.randomUUID().toString() to "") },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = NavyText)
            ) {
                Text("+", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            FormLabel("Recordatorios")
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                reminderOptions.forEach { days ->
                    val isSelected = selectedReminders.contains(days)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) selectedReminders.remove(days) else selectedReminders.add(days)
                        },
                        label = { Text(reminderLabels[days] ?: "${days}d") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = YellowPrimary,
                            selectedLabelColor = NavyText,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { 
                    if (taskName.isBlank() || selectedSubjectId.isNullOrBlank() || dueDate.isBlank() || importance.isBlank() || complexity.isBlank()) {
                        showValidationError = true
                    } else {
                        val taskId = java.util.UUID.randomUUID().toString()
                        
                        val importanceValue = when(importance) {
                            "Muy urgente" -> 0.9
                            "Algo urgente" -> 0.5
                            "Muy poco urgente" -> 0.2
                            else -> 0.5
                        }
                        
                        val complexityValue = when(complexity) {
                            "Alta" -> 5
                            "Media" -> 3
                            "Baja" -> 1
                            else -> 3
                        }
                        
                        val importanceInt = when(importance) {
                            "Muy urgente" -> 5
                            "Algo urgente" -> 3
                            "Muy poco urgente" -> 1
                            else -> 3
                        }
                        
                        val newTask = com.example.workminder.data.model.Task(
                            id = taskId,
                            task_title = taskName,
                            due_date = dueDate,
                            urgency = importanceValue,
                            importance = importanceInt,
                            complexity = complexityValue,
                            notes = notes,
                            subject_id = selectedSubjectId,
                            subtasks = subtasks.filter { it.second.isNotBlank() }.map { 
                                Subtask(java.util.UUID.randomUUID().toString(), taskId, it.second) 
                            },
                            reminders = selectedReminders.toList()
                        )
                        viewModel.createTask(newTask)
                        navController.popBackStack()
                    }
                },
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
