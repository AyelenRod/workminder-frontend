package com.example.workminder.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
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
import com.example.workminder.data.model.Subtask
import com.example.workminder.ui.components.WorkMinderTopBar
import com.example.workminder.ui.components.WorkMinderDialog
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workminder.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditTaskScreen(taskId: String, navController: NavController, viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val original = viewModel.tasks.find { it.id == taskId } ?: run {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    var taskName    by remember { mutableStateOf(original.task_title) }
    var selectedSubjectId by remember { mutableStateOf(original.subject_id ?: "none") }
    var selectedSubjectName by remember { 
        mutableStateOf(viewModel.subjects.find { it.id == original.subject_id }?.subject_name ?: "Sin materia") 
    }
    var dueDateStr by remember { mutableStateOf(original.due_date) }
    var importance  by remember { 
        mutableStateOf(com.example.workminder.data.model.TaskLevel.fromInt(original.importance).displayName) 
    }
    var complexity  by remember { 
        mutableStateOf(com.example.workminder.data.model.TaskLevel.fromInt(original.complexity).displayName) 
    }
    var notes       by remember { mutableStateOf(original.notes) }
    val subtasks = remember {
        val initialList = original.subtasks.map { it.subtask_id to it.subtask_name }
        mutableStateListOf(*initialList.toTypedArray()).also {
            if (it.isEmpty()) it.add(java.util.UUID.randomUUID().toString() to "")
        }
    }
    val selectedReminders = remember { 
        val list = mutableStateListOf<com.example.workminder.data.model.Reminder>()
        list.addAll(original.reminders)
        list
    }

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
                name = viewModel.userName,
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
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = NavyText)
                }
                Text("Editar tarea", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = NavyText)
            }

            Spacer(modifier = Modifier.height(20.dp))

            EditLabel("Nombre de la tarea")
            OutlinedTextField(
                value = taskName, onValueChange = { if (it.length <= 100) taskName = it },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), singleLine = true,
                colors = editFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))

            EditLabel("Materia")
            ExposedDropdownMenuBox(expanded = subjectExpanded, onExpandedChange = { subjectExpanded = it }) {
                OutlinedTextField(
                    value = selectedSubjectName, onValueChange = {}, readOnly = true,
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, null, tint = NavyText) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(8.dp), colors = editFieldColors()
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
            Spacer(modifier = Modifier.height(16.dp))

            var showDatePicker by remember { mutableStateOf(false) }
            if (showDatePicker) {
                val calendar = java.util.Calendar.getInstance()
                android.app.DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        dueDateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    },
                    calendar.get(java.util.Calendar.YEAR),
                    calendar.get(java.util.Calendar.MONTH),
                    calendar.get(java.util.Calendar.DAY_OF_MONTH)
                ).show()
                showDatePicker = false
            }

            EditLabel("Fecha de entrega")
            OutlinedTextField(
                value = dueDateStr, onValueChange = { dueDateStr = it },
                readOnly = true,
                placeholder = { Text("Selecciona una fecha", color = TextSecondary) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.CalendarMonth, null, tint = TextSecondary)
                    }
                },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                shape = RoundedCornerShape(8.dp), singleLine = true, colors = editFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))

            EditLabel("Importancia")
            ExposedDropdownMenuBox(expanded = importanceExpanded, onExpandedChange = { importanceExpanded = it }) {
                OutlinedTextField(
                    value = importance, onValueChange = {}, readOnly = true,
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, null, tint = NavyText) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(8.dp), colors = editFieldColors()
                )
                ExposedDropdownMenu(expanded = importanceExpanded, onDismissRequest = { importanceExpanded = false }) {
                    levels.forEach { opt -> DropdownMenuItem(text = { Text(opt) }, onClick = { importance = opt; importanceExpanded = false }) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            EditLabel("Complejidad")
            ExposedDropdownMenuBox(expanded = complexityExpanded, onExpandedChange = { complexityExpanded = it }) {
                OutlinedTextField(
                    value = complexity, onValueChange = {}, readOnly = true,
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, null, tint = NavyText) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(8.dp), colors = editFieldColors()
                )
                ExposedDropdownMenu(expanded = complexityExpanded, onDismissRequest = { complexityExpanded = false }) {
                    levels.forEach { opt -> DropdownMenuItem(text = { Text(opt) }, onClick = { complexity = opt; complexityExpanded = false }) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            EditLabel("Notas extra (${notes.length}/200)")
            OutlinedTextField(
                value = notes, onValueChange = { if (it.length <= 200) notes = it },
                placeholder = { Text("Opcional", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(8.dp),
                maxLines = 4, colors = editFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))

            EditLabel("Subtareas")
            subtasks.forEachIndexed { index, sub ->
                key(sub.first) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = sub.second, onValueChange = { subtasks[index] = sub.first to it },
                            placeholder = { Text("Nombre de la subtarea", color = TextSecondary) },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                            singleLine = true, colors = editFieldColors()
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

            EditLabel("Recordatorios")
            selectedReminders.forEachIndexed { index, reminder ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(reminder.reminderDate, color = NavyText, modifier = Modifier.weight(1f))
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
                            val fullDateStr = String.format("%04d-%02d-%02dT%02d:%02d:00Z", y, m + 1, d, hh, mm)
                            try {
                                val now = java.time.LocalDateTime.now()
                                val selectedDate = java.time.LocalDateTime.of(y, m+1, d, hh, mm)
                                val deadline = java.time.LocalDate.parse(dueDateStr).atTime(23, 59)
                                
                                if (selectedDate.isBefore(now)) {
                                    validationMessage = "El recordatorio no puede ser en el pasado."
                                    showValidationError = true
                                } else if (selectedDate.isAfter(deadline)) {
                                    validationMessage = "El recordatorio no puede ser después de la fecha de entrega."
                                    showValidationError = true
                                } else {
                                    selectedReminders.add(com.example.workminder.data.model.Reminder(fullDateStr))
                                }
                            } catch (e: Exception) {
                                selectedReminders.add(com.example.workminder.data.model.Reminder(fullDateStr))
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
                    if (taskName.isBlank() || dueDateStr.isBlank() || importance.isBlank() || complexity.isBlank()) {
                        validationMessage = "Por favor, completa todos los campos obligatorios."
                        showValidationError = true
                    } else {
                        val levelImp = com.example.workminder.data.model.TaskLevel.fromDisplayName(importance)
                        val levelComp = com.example.workminder.data.model.TaskLevel.fromDisplayName(complexity)
                        
                        val urgencyCalculated = com.example.workminder.data.model.calculateUrgency(
                            levelImp.value, levelComp.value, dueDateStr
                        )

                        val updated = original.copy(
                            task_title = taskName,
                            due_date = dueDateStr,
                            urgency = urgencyCalculated,
                            importance = levelImp.value,
                            complexity = levelComp.value,
                            extra_note = if (notes.isBlank()) null else notes,
                            subject_id = if (selectedSubjectId == "none") null else selectedSubjectId,
                            subtasks = subtasks.filter { it.second.isNotBlank() }.map { subPair ->
                                val existing = original.subtasks.find { it.subtask_id == subPair.first }
                                com.example.workminder.data.model.Subtask(
                                    subtask_id = subPair.first,
                                    task_id = original.id,
                                    subtask_name = subPair.second,
                                    is_completed = existing?.is_completed ?: false
                                )
                            },
                            reminders = selectedReminders.toList()
                        )
                        
                        viewModel.updateTask(updated)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaveGreen, contentColor = Color.White)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Aceptar cambios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun EditLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
        color = NavyText, modifier = Modifier.padding(bottom = 6.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun editFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = YellowPrimary,
    unfocusedBorderColor = YellowPrimary,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    focusedTextColor = TextSecondary,
    unfocusedTextColor = TextSecondary
)
