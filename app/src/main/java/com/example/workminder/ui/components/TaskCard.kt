package com.example.workminder.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.workminder.data.model.Task
import com.example.workminder.data.model.TaskUrgency
import com.example.workminder.data.model.getTaskUrgency
import com.example.workminder.ui.theme.*

@Composable
fun TaskCard(
    task: Task,
    subjectName: String, // Cambiado de 'subtitle' a 'subjectName' para sincronizar
    subjectColor: String, // Agregado para recibir el color de la materia
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    // Convertimos el valor numérico (Double) de la DB al Enum visual
    val urgencyLevel = getTaskUrgency(task.urgency)

    val accentColor = when (urgencyLevel) {
        TaskUrgency.HIGH   -> UrgentRed
        TaskUrgency.MEDIUM -> UrgentYellow
        TaskUrgency.LOW    -> UrgentCyan
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape  = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, accentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyText,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = task.displayDate, // Formato DD/MM/YYYY definido en Task.kt
                    style = MaterialTheme.typography.bodySmall,
                    color = NavyText
                )
            }

            Text(
                text = subjectName, // Usamos el nombre que calculamos en AgendaScreen
                style = MaterialTheme.typography.bodyMedium,
                color = Color(android.graphics.Color.parseColor(subjectColor)).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${task.status?.displayName ?: "Pendiente"} - ${urgencyLevel.displayName}",
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onAddClick() },
                    contentAlignment = Alignment.Center
                ) {
<<<<<<< Updated upstream
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Ver detalles",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
=======
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Completar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
>>>>>>> Stashed changes
                }
            }
        }
    }
}