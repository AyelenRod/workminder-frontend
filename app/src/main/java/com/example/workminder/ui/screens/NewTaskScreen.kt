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
    var selectedSubjectId by remember { mutableStateOf("none") }
    var selectedSubjectName by remember { mutableStateOf("Sin materia") }
    var dueDate     by remember { mutableStateOf("") }
    var importance  by remember { mutableStateOf("Media") }
    var complexity  by remember { mutableStateOf("Media") }
    var notes       by remember { mutableStateOf("") }
    val subtasks    = remember { mutableStateListOf(java.util.UUID.randomUUID().toString() to "") }
    val selectedReminders = remember { mutableStateListOf<String>() }

    var importanceExpanded by remember { mutableStateOf(false) }
    var complexityExpanded by remember { mutableStateOf(false) }
    var subjectExpanded by remember { mutableStateOf(false) }

    var showValidationError by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }

    val levels = com.example.workminder.data.model.TaskLevel.entries.map { it.displayName }.reversed()

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
                title = "Atención",
                message = validationMessage,
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
                onValueChange = { if (it.length <= 100) taskName = it },
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
                    DropdownMenuItem(text = { Text("Sin materia") }, onClick = { 
                        selectedSubjectName = "Sin materia"
                        selectedSubjectId = "none"
                        subjectExpanded = false 
                    })
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
                    levels.forEach { opt ->
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
                    levels.forEach { opt ->
                        DropdownMenuItem(text = { Text(opt) }, onClick = { complexity = opt; complexityExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            FormLabel("Notas extra (${notes.length}/200)")
            OutlinedTextField(
                value = notes,
                onValueChange = { if (it.length <= 200) notes = it },
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
            selectedReminders.forEachIndexed { index, dateStr ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(dateStr, color = NavyText, modifier = Modifier.weight(1f))
                    IconButton(onClick = { selectedReminders.removeAt(index) }) {
                        Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Button(
                onClick = {
                    val cal = java.util.Calendar.getInstance()
                    android.app.DatePickerDialog(context, { _, y, m, d ->
                        android.app.TimePickerDialog(context, { _, hh, mm ->
                            val fullDateStr = String.format("%04d-%02d-%02d %02d:%02d", y, m + 1, d, hh, mm)
                            // Validar fechas de recordatorio
                            try {
                                val now = java.time.LocalDateTime.now()
                                val selectedDate = java.time.LocalDateTime.of(y, m+1, d, hh, mm)
                                
                                if (dueDate.isNotBlank()) {
                                    val deadline = java.time.LocalDate.parse(dueDate).atTime(23, 59)
                                    if (selectedDate.isBefore(now)) {
                                        validationMessage = "El recordatorio no puede ser en el pasado."
                                        showValidationError = true
                                    } else if (selectedDate.isAfter(deadline)) {
                                        validationMessage = "El recordatorio no puede ser después de la fecha de entrega."
                                        showValidationError = true
                                    } else {
                                        selectedReminders.add(fullDateStr)
                                    }
                                } else {
                                    validationMessage = "Indica primero la fecha de entrega."
                                    showValidationError = true
                                }
                            } catch (e: Exception) {
                                selectedReminders.add(fullDateStr)
                            }
                        }, cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE), true).show()
                    }, cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH), cal.get(java.util.Calendar.DAY_OF_MONTH)).show()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = NavyText)
            ) {
                Text("Añadir recordatorio", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    if (taskName.isBlank() || dueDate.isBlank() || importance.isBlank() || complexity.isBlank()) {
                        validationMessage = "Por favor, completa todos los campos obligatorios."
                        showValidationError = true
                    } else {
                        val taskId = java.util.UUID.randomUUID().toString()
                        val levelImp = com.example.workminder.data.model.TaskLevel.fromDisplayName(importance)
                        val levelComp = com.example.workminder.data.model.TaskLevel.fromDisplayName(complexity)
                        
                        val urgencyCalculated = com.example.workminder.data.model.calculateUrgency(
                            levelImp.value, levelComp.value, dueDate
                        )
                        
                        val newTask = com.example.workminder.data.model.Task(
                            id = taskId,
                            task_title = taskName,
                            due_date = dueDate,
                            urgency = urgencyCalculated,
                            importance = levelImp.value,
                            complexity = levelComp.value,
                            notes = notes,
                            subject_id = if (selectedSubjectId == "none") null else selectedSubjectId,
                            subtasks = subtasks.filter { it.second.isNotBlank() }.map { 
                                com.example.workminder.data.model.Subtask(java.util.UUID.randomUUID().toString(), taskId, it.second) 
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
