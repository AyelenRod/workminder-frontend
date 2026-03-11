package com.example.workminder.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workminder.data.model.MockData
import com.example.workminder.data.model.TaskUrgency
import com.example.workminder.ui.components.WorkMinderTopBar
import com.example.workminder.ui.navigation.NavRoutes
import androidx.compose.runtime.*
import com.example.workminder.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workminder.ui.viewmodel.MainViewModel

@Composable
fun TaskDetailScreen(taskId: String, navController: NavController, viewModel: MainViewModel = viewModel()) {
    val task = viewModel.tasks.find { it.id == taskId } ?: run {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val urgencyInfo = com.example.workminder.data.model.getUrgencyLevel(task.urgency)
    val accentColorStr = com.example.workminder.data.model.getUrgencyColor(task.urgency)
    val accentColor = Color(android.graphics.Color.parseColor(accentColorStr))

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar", tint = NavyText)
                }
                Text(
                    text = "Detalles de tarea",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.width(6.dp).height(intrinsicSize = IntrinsicSize.Max).background(accentColor))
                    
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text(text = task.task_title, style = MaterialTheme.typography.headlineMedium, color = NavyText, fontWeight = FontWeight.Bold)
                        Text(text = viewModel.subjects.find { it.id == task.subject_id }?.subject_name ?: "Sin materia", style = MaterialTheme.typography.titleMedium, color = NavyText, fontWeight = FontWeight.SemiBold)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DetailItem(icon = Icons.Filled.AccessTime, label = "Fecha de entrega:", value = task.due_date)
                        DetailItem(icon = Icons.Filled.PriorityHigh, label = "Urgencia:", value = urgencyInfo.displayName, valueColor = accentColor)
                        DetailItem(
                            icon = Icons.Filled.TextFormat, 
                            label = "Importancia:", 
                            value = com.example.workminder.data.model.TaskLevel.fromInt(task.importance).displayName
                        ) 
                        DetailItem(
                            icon = Icons.Filled.Description, 
                            label = "Complejidad:", 
                            value = com.example.workminder.data.model.TaskLevel.fromInt(task.complexity).displayName
                        )
                        DetailItem(
                            icon = Icons.Filled.CheckCircleOutline, 
                            label = "Estado:", 
                            value = task.status?.displayName ?: "Pendiente", 
                            valueColor = when(task.status) {
                                com.example.workminder.data.model.TaskStatus.DONE -> SaveGreen
                                com.example.workminder.data.model.TaskStatus.LATE -> Level5Red
                                else -> Level4Orange
                            }
                        )

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = accentColor.copy(alpha = 0.3f))

                        if (task.notes.isNotBlank()) {
                            Text("Notas extra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = task.notes, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = accentColor.copy(alpha = 0.3f))
                        }

                        if (task.subtasks.isNotEmpty()) {
                            Text("Subtareas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyText)
                            Spacer(modifier = Modifier.height(8.dp))
                            task.subtasks.forEachIndexed { idx, sub ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { 
                                            val updatedSubtasks = task.subtasks.toMutableList()
                                            updatedSubtasks[idx] = sub.copy(is_completed = !sub.is_completed)
                                            viewModel.updateTask(task.copy(subtasks = updatedSubtasks))
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Filled.SubdirectoryArrowRight, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = sub.subtask_name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                textDecoration = if (sub.is_completed) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                            ),
                                            color = if (sub.is_completed) TextSecondary.copy(alpha = 0.5f) else TextSecondary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Icon(
                                        imageVector = if (sub.is_completed) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                                        contentDescription = null,
                                        tint = NavyText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = accentColor.copy(alpha = 0.3f))
                        }

                        if (task.reminders.isNotEmpty()) {
                            Text("Recordatorios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyText)
                            Spacer(modifier = Modifier.height(8.dp))
                            task.reminders.forEach { reminder ->
                                Surface(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, NavyText.copy(alpha = 0.3f)),
                                    color = Color.White
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Notifications, null, tint = NavyText, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(reminder.reminderDate, style = MaterialTheme.typography.bodySmall, color = NavyText, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = accentColor.copy(alpha = 0.3f))
                        }
                        
                        // Action buttons
                        Text("Acciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (task.status != com.example.workminder.data.model.TaskStatus.DONE) {
                                Button(
                                    onClick = {
                                        val timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_DATE_TIME)
                                        viewModel.updateTask(task.copy(status = com.example.workminder.data.model.TaskStatus.DONE, completed_at = timestamp))
                                    },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaveGreen, contentColor = Color.White)
                                ) {
                                    Text("Completar", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        val today = java.time.LocalDate.now()
                                        val due = try {
                                            java.time.LocalDate.parse(task.due_date.split("T")[0], java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
                                        } catch (e: Exception) { today }
                                        val newStatus = if (due.isBefore(today)) com.example.workminder.data.model.TaskStatus.LATE else com.example.workminder.data.model.TaskStatus.PENDING
                                        viewModel.updateTask(task.copy(status = newStatus, completed_at = null))
                                    },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(2.dp, YellowPrimary),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyText)
                                ) {
                                    Text("Reabrir", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { navController.navigate(NavRoutes.EditTask.createRoute(task.id)) },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyText, contentColor = Color.White)
                            ) {
                                Icon(Icons.Filled.Edit, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Editar", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { 
                                    viewModel.deleteTask(task)
                                    navController.popBackStack()
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Level5Red, contentColor = Color.White)
                            ) {
                                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Eliminar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, valueColor: Color = NavyText) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = NavyText, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = NavyText, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = valueColor, fontWeight = FontWeight.SemiBold)
    }
}
