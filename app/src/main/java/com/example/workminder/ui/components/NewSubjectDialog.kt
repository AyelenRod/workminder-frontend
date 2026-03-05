package com.example.workminder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.workminder.data.model.MockData
import com.example.workminder.data.model.Subject
import com.example.workminder.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSubjectDialog(
    onDismissRequest: () -> Unit,
    onSubjectCreated: () -> Unit
) {
    var subjectName by remember { mutableStateOf("") }
    
    val defaultColors = listOf(
        "#FF5722", "#3F51B5", "#009688", "#E91E63", "#9C27B0", "#FFC107", "#4CAF50", "#00BCD4"
    )
    var selectedColor by remember { mutableStateOf(defaultColors[0]) }

    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nueva materia",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyText
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it; showError = false },
                    placeholder = { Text("Nombre de la materia", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    isError = showError && subjectName.isBlank(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YellowPrimary,
                        unfocusedBorderColor = NavyText.copy(alpha = 0.35f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Selecciona un color:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyText,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color palette
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    defaultColors.take(4).forEach { colorHex ->
                        ColorCircle(
                            colorHex = colorHex,
                            isSelected = colorHex == selectedColor,
                            onClick = { selectedColor = colorHex }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    defaultColors.drop(4).take(4).forEach { colorHex ->
                        ColorCircle(
                            colorHex = colorHex,
                            isSelected = colorHex == selectedColor,
                            onClick = { selectedColor = colorHex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyText)
                    ) {
                        Text("Cancelar", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (subjectName.isBlank()) {
                                showError = true
                            } else {
                                val newSubject = Subject(
                                    id = MockData.subjects.maxOfOrNull { it.id }?.plus(1) ?: 1,
                                    name = subjectName,
                                    colorHex = selectedColor
                                )
                                MockData.subjects.add(newSubject)
                                onSubjectCreated()
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = NavyText)
                    ) {
                        Text("Aceptar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorCircle(colorHex: String, isSelected: Boolean, onClick: () -> Unit) {
    val color = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { Color.Gray }
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}
