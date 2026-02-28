package com.example.workminder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.workminder.data.model.MockData
import com.example.workminder.data.model.Task
import com.example.workminder.ui.components.BottomNavBar
import com.example.workminder.ui.components.FilterDialog
import com.example.workminder.ui.components.TaskCard
import com.example.workminder.ui.components.WorkMinderTopBar
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.*

@Composable
fun AgendaScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }
    var showFilter by remember { mutableStateOf(false) }

    var thisWeekExpanded by remember { mutableStateOf(true) }
    var nextWeekExpanded by remember { mutableStateOf(false) }
    var laterExpanded by remember { mutableStateOf(false) }

    if (showFilter) {
        FilterDialog(onDismiss = { showFilter = false })
    }

    Scaffold(
        topBar = {
            WorkMinderTopBar(
                subtitle = "La Agenda de",
                name = MockData.userName,
                onSettingsClick = { navController.navigate(NavRoutes.Settings.route) }
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.NewTask.route) },
                containerColor = YellowPrimary,
                contentColor = NavyText,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva tarea")
            }
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Search bar + filter button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Buscar tarea", color = TextSecondary) },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = TextSecondary)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = YellowPrimary,
                            unfocusedBorderColor = TextSecondary,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        )
                    )
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showFilter = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = YellowPrimary,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = "Filtros",
                                    tint = NavyText
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Esta semana
            item {
                TaskGroup(
                    label = "Esta semana",
                    count = MockData.thisWeekTasks.size,
                    expanded = thisWeekExpanded,
                    onToggle = { thisWeekExpanded = !thisWeekExpanded }
                )
            }
            item {
                AnimatedVisibility(visible = thisWeekExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        MockData.thisWeekTasks.forEach { task ->
                            TaskCard(
                                task = task,
                                onClick = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            // Siguiente semana
            item {
                TaskGroup(
                    label = "Siguiente semana",
                    count = MockData.nextWeekTasks.size,
                    expanded = nextWeekExpanded,
                    onToggle = { nextWeekExpanded = !nextWeekExpanded }
                )
            }
            item {
                AnimatedVisibility(visible = nextWeekExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        MockData.nextWeekTasks.forEach { task ->
                            TaskCard(
                                task = task,
                                onClick = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            // Más tarde
            item {
                TaskGroup(
                    label = "Más tarde",
                    count = MockData.laterTasks.size,
                    expanded = laterExpanded,
                    onToggle = { laterExpanded = !laterExpanded }
                )
            }
            item {
                AnimatedVisibility(visible = laterExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        MockData.laterTasks.forEach { task ->
                            TaskCard(
                                task = task,
                                onClick = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TaskGroup(
    label: String,
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = NavyText
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = YellowPrimary
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = YellowPrimary
            )
        }
    }
    Divider(color = NavyText.copy(alpha = 0.15f))
}
