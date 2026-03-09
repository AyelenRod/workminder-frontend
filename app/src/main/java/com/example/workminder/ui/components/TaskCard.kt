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
import com.example.workminder.ui.theme.*

@Composable
fun TaskCard(
    task: Task,
    subjectName: String = "Sin materia",
    subjectColor: String = "#808080",
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val urgencyLevel = com.example.workminder.data.model.getUrgencyLevel(task.urgency)
    val urgencyColorHex = com.example.workminder.data.model.getUrgencyColor(task.urgency)
    val accentColor = try { Color(android.graphics.Color.parseColor(urgencyColorHex)) } catch (e: Exception) { Level3Yellow }

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
            // Title + date row
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
                    text = task.displayDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = NavyText
                )
            }
            // Subject
            Text(
                text = subjectName,
                style = MaterialTheme.typography.bodyMedium,
                color = try { Color(android.graphics.Color.parseColor(subjectColor)) } catch (e: Exception) { TextSecondary }
            )
            Spacer(modifier = Modifier.height(6.dp))
            // Status + urgency + button
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
                }
            }
        }
    }
}
