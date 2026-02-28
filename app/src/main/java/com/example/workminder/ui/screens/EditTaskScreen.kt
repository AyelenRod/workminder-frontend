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
fun EditTaskScreen(taskId: Int, navController: NavController) {
    val original = MockData.tasks.find { it.id == taskId } ?: MockData.tasks.first()

    var taskName    by remember { mutableStateOf(original.title) }
    var subject     by remember { mutableStateOf(original.subject) }
    var dueDate     by remember { mutableStateOf(original.dueDate) }
    var importance  by remember { mutableStateOf(original.urgency.displayName) }
    var complexity  by remember { mutableStateOf(original.complexity) }
    var notes       by remember { mutableStateOf(original.notes) }
    val subtasks    = remember { mutableStateListOf(*original.subtasks.toTypedArray()) }
    if (subtasks.isEmpty()) subtasks.add("")

    var subjectExpanded    by remember { mutableStateOf(false) }
    var importanceExpanded by remember { mutableStateOf(false) }
    var complexityExpanded by remember { mutableStateOf(false) }

    val subjects     = listOf("Proyecto Integrador 2", "Cálculo Diferencial", "Física", "Química", "Sistemas Operativos")
    val importances  = listOf("Muy urgente", "Algo urgente", "Muy poco urgente")
    val complexities = listOf("Alta", "Media", "Baja")

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
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = NavyText)
                }
                Text("Editar tarea", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = NavyText)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Task name
            EditLabel("Nombre de la tarea")
            OutlinedTextField(
                value = taskName, onValueChange = { taskName = it },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), singleLine = true,
                colors = editFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Subject
            EditLabel("Materia")
            ExposedDropdownMenuBox(expanded = subjectExpanded, onExpandedChange = { subjectExpanded = it }) {
                OutlinedTextField(
                    value = subject, onValueChange = {}, readOnly = true,
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, null, tint = NavyText) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(8.dp), colors = editFieldColors()
                )
                ExposedDropdownMenu(expanded = subjectExpanded, onDismissRequest = { subjectExpanded = false }) {
                    subjects.forEach { opt -> DropdownMenuItem(text = { Text(opt) }, onClick = { subject = opt; subjectExpanded = false }) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Due date
            EditLabel("Fecha de entrega")
            OutlinedTextField(
                value = dueDate, onValueChange = { dueDate = it },
                trailingIcon = { Icon(Icons.Filled.CalendarMonth, null, tint = TextSecondary) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), singleLine = true, colors = editFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Importance
            EditLabel("Importancia")
            ExposedDropdownMenuBox(expanded = importanceExpanded, onExpandedChange = { importanceExpanded = it }) {
                OutlinedTextField(
                    value = importance, onValueChange = {}, readOnly = true,
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, null, tint = NavyText) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(8.dp), colors = editFieldColors()
                )
                ExposedDropdownMenu(expanded = importanceExpanded, onDismissRequest = { importanceExpanded = false }) {
                    importances.forEach { opt -> DropdownMenuItem(text = { Text(opt) }, onClick = { importance = opt; importanceExpanded = false }) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Complexity
            EditLabel("Complejidad")
            ExposedDropdownMenuBox(expanded = complexityExpanded, onExpandedChange = { complexityExpanded = it }) {
                OutlinedTextField(
                    value = complexity, onValueChange = {}, readOnly = true,
                    trailingIcon = { Icon(Icons.Filled.KeyboardArrowDown, null, tint = NavyText) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(8.dp), colors = editFieldColors()
                )
                ExposedDropdownMenu(expanded = complexityExpanded, onDismissRequest = { complexityExpanded = false }) {
                    complexities.forEach { opt -> DropdownMenuItem(text = { Text(opt) }, onClick = { complexity = opt; complexityExpanded = false }) }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Notes
            EditLabel("Notas extra")
            OutlinedTextField(
                value = notes, onValueChange = { notes = it },
                placeholder = { Text("Opcional", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(8.dp),
                maxLines = 4, colors = editFieldColors()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Subtasks
            EditLabel("Subtareas")
            subtasks.forEachIndexed { index, sub ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = sub, onValueChange = { subtasks[index] = it },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                        singleLine = true, colors = editFieldColors()
                    )
                    IconButton(onClick = { if (subtasks.size > 1) subtasks.removeAt(index) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = UrgentRed)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, YellowPrimary),
                color = Color.White,
                modifier = Modifier.size(width = 48.dp, height = 36.dp).clickable { subtasks.add("") }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("+", color = NavyText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                }
            }
            

            Spacer(modifier = Modifier.height(32.dp))

            // Save button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaveGreen, contentColor = Color.White)
            ) {
                Text("Guardar cambios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
