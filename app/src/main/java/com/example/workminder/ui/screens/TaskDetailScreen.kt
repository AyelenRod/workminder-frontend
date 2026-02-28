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
import com.example.workminder.ui.theme.*

@Composable
fun TaskDetailScreen(taskId: Int, navController: NavController) {
    val task = MockData.tasks.find { it.id == taskId } ?: MockData.tasks.first()

    val accentColor = when (task.urgency) {
        TaskUrgency.HIGH   -> UrgentRed
        TaskUrgency.MEDIUM -> UrgentYellow
        TaskUrgency.LOW    -> UrgentCyan
    }

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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back button row
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

            // Main Card (Design with left border)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.LightGray),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Left color bar
                    Box(modifier = Modifier.width(6.dp).height(500.dp).background(accentColor))
                    
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        Text(text = task.title, style = MaterialTheme.typography.headlineMedium, color = NavyText, fontWeight = FontWeight.Bold)
                        Text(text = task.subject, style = MaterialTheme.typography.titleMedium, color = NavyText, fontWeight = FontWeight.SemiBold)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        DetailItem(icon = Icons.Filled.AccessTime, label = "Fecha de entrega:", value = task.dueDate)
                        DetailItem(icon = Icons.Filled.PriorityHigh, label = "Urgencia:", value = task.urgency.displayName, valueColor = accentColor)
                        DetailItem(icon = Icons.Filled.TextFormat, label = "Importancia:", value = "Alta", valueColor = UrgentYellow) // Mocked A+ icon equivalent
                        DetailItem(icon = Icons.Filled.Description, label = "Complejidad:", value = task.complexity, valueColor = UrgentYellow)
                        DetailItem(icon = Icons.Filled.CheckCircleOutline, label = "Estado:", value = task.status.displayName, valueColor = UrgentYellow)

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = NavyText.copy(alpha = 0.2f))

                        if (task.notes.isNotBlank()) {
                            Text("Notas extra", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyText)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = task.notes, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = NavyText.copy(alpha = 0.2f))
                        }

                        if (task.subtasks.isNotEmpty()) {
                            Text("Subtareas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyText)
                            Spacer(modifier = Modifier.height(8.dp))
                            task.subtasks.forEach { sub ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.SubdirectoryArrowRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(sub, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                                    }
                                    Icon(Icons.Filled.CheckBoxOutlineBlank, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = NavyText.copy(alpha = 0.2f))
                        }

                        // Recordatorios
                        Text("Recordatorios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, NavyText.copy(alpha = 0.3f)),
                            color = Color.White
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Notifications, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("1 día antes, 10:00 PM", style = MaterialTheme.typography.bodySmall, color = NavyText, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = NavyText.copy(alpha = 0.2f))

                        // Marcar como
                        Text("Marcar como", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = NavyText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { /* TODO */ },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, SaveGreen),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SaveGreen)
                            ) {
                                Text("Completada", fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = { /* TODO */ },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.5.dp, YellowPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = YellowPrimary)
                            ) {
                                Text("En progreso", fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = NavyText.copy(alpha = 0.2f))

                        // Editar/Eliminar
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { navController.navigate(NavRoutes.EditTask.createRoute(task.id)) },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A4A8A), contentColor = Color.White) // Purple/Navy mix from design
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Editar", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = UrgentRed, contentColor = Color.White)
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
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
