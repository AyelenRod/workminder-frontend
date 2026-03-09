package com.example.workminder.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.workminder.data.model.MockData
import com.example.workminder.ui.components.BottomNavBar
import com.example.workminder.ui.components.FilterDialog
import com.example.workminder.ui.components.NewSubjectDialog
import com.example.workminder.ui.components.SubjectCard
import com.example.workminder.ui.components.TaskCard
import com.example.workminder.ui.components.WorkMinderDialog
import com.example.workminder.ui.components.WorkMinderTopBar
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workminder.ui.viewmodel.MainViewModel
import com.example.workminder.data.model.Subject

@Composable
fun AgendaScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    var showFilter by remember { mutableStateOf(false) }
    var activeStatusFilter by remember { mutableStateOf("Pendientes") }
    var activeGroupBy by remember { mutableStateOf("Urgencia") }

    var viewMode by remember { mutableStateOf("Tareas") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showNewSubjectDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<com.example.workminder.data.model.Subject?>(null) }

    val expandedGroups = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    val filteredTasks = remember(viewModel.tasks.size, query, activeStatusFilter, activeGroupBy) {
        var list = viewModel.tasks.toList()

        // Búsqueda
        if (query.isNotBlank()) {
            list = list.filter { 
                it.title.contains(query, ignoreCase = true) || 
                viewModel.subjects.find { s -> s.id == it.subject_id }?.subject_name?.contains(query, ignoreCase = true) == true
            }
        }

        // Filtro de estatus
        list = when (activeStatusFilter) {
            "Pendientes" -> list.filter { it.status == com.example.workminder.data.model.TaskStatus.PENDING }
            "Atrasadas"  -> list.filter { it.status == com.example.workminder.data.model.TaskStatus.LATE }.sortedByDescending { it.due_date }
            "Completadas" -> list.filter { it.status == com.example.workminder.data.model.TaskStatus.DONE }.sortedByDescending { it.completed_at }
            else -> list
        }
        list
    }

    val groupedTasks = remember(filteredTasks, activeGroupBy, activeStatusFilter) {
        if (activeStatusFilter == "Completadas" || activeStatusFilter == "Atrasadas") {
            null // No agrupar
        } else {
            when (activeGroupBy) {
                "Urgencia" -> filteredTasks.groupBy { com.example.workminder.data.model.getUrgencyLevel(it.urgency).displayName }
                "Importancia" -> filteredTasks.groupBy { com.example.workminder.data.model.TaskLevel.fromInt(it.importance).displayName }
                "Complejidad" -> filteredTasks.groupBy { com.example.workminder.data.model.TaskLevel.fromInt(it.complexity).displayName }
                "Fecha de entrega" -> {
                    val now = java.time.LocalDate.now()
                    val nextWeek = now.plusDays(7)
                    filteredTasks.groupBy { task ->
                        try {
                            val date = java.time.LocalDate.parse(task.due_date)
                            when {
                                !date.isAfter(nextWeek) -> "Esta semana"
                                !date.isAfter(now.plusDays(14)) -> "Siguiente semana"
                                else -> "Posteriores"
                            }
                        } catch (e: Exception) { "Posteriores" }
                    }
                }
                else -> null
            }
        }
    }

    if (showFilter) {
        FilterDialog(
            onDismiss = { showFilter = false },
            onApply   = { groupBy, filter ->
                activeGroupBy      = groupBy
                activeStatusFilter = filter
                showFilter         = false
            }
        )
    }

    if (showNewSubjectDialog || editingSubject != null) {
        NewSubjectDialog(
            editingSubject = editingSubject,
            onDismissRequest = { 
                showNewSubjectDialog = false 
                editingSubject = null
            },
            onSubjectCreated = { 
                showNewSubjectDialog = false 
                editingSubject = null
            }
        )
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "¿Qué deseas agregar?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = NavyText)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { showAddDialog = false; navController.navigate(NavRoutes.NewTask.route) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = NavyText)
                    ) { Text("Nueva tarea", fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { showAddDialog = false; showNewSubjectDialog = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyText)
                    ) { Text("Nueva materia", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            WorkMinderTopBar(subtitle = "La Agenda de", name = "Usuario", onSettingsClick = { navController.navigate(NavRoutes.Settings.route) })
        },
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = YellowPrimary, contentColor = NavyText, shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        },
        containerColor = BackgroundGray
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = query, onValueChange = { query = it }, placeholder = { Text("Buscar por nombre o materia...", color = TextSecondary) },
                        leadingIcon = { Icon(Icons.Filled.Search, null, tint = TextSecondary) },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = YellowPrimary, unfocusedBorderColor = TextSecondary, unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                    )
                    if (viewMode == "Tareas") {
                        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).clickable { showFilter = true }, contentAlignment = Alignment.Center) {
                            Surface(shape = RoundedCornerShape(12.dp), color = YellowPrimary, modifier = Modifier.fillMaxSize()) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Tune, null, tint = NavyText) }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewMode = "Tareas" },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (viewMode == "Tareas") YellowPrimary else TextSecondary),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (viewMode == "Tareas") YellowPrimary.copy(alpha=0.1f) else Color.Transparent, contentColor = if (viewMode == "Tareas") NavyText else TextSecondary)
                    ) { Text("Tareas", fontWeight = if (viewMode == "Tareas") FontWeight.Bold else FontWeight.Normal) }
                    OutlinedButton(
                        onClick = { viewMode = "Materias" },
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (viewMode == "Materias") YellowPrimary else TextSecondary),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (viewMode == "Materias") YellowPrimary.copy(alpha=0.1f) else Color.Transparent, contentColor = if (viewMode == "Materias") NavyText else TextSecondary)
                    ) { Text("Materias", fontWeight = if (viewMode == "Materias") FontWeight.Bold else FontWeight.Normal) }
                }
            }

            if (viewMode == "Tareas") {
                if (groupedTasks != null) {
                    groupedTasks.forEach { (label, tasks) ->
                        val isExpanded = expandedGroups[label] ?: true
                        item {
                            TaskGroup(
                                label = label,
                                count = tasks.size,
                                expanded = isExpanded,
                                onToggle = { expandedGroups[label] = !isExpanded }
                            )
                        }
                        if (isExpanded) {
                            items(tasks) { task ->
                                val subj = viewModel.subjects.find { it.id == task.subject_id }
                                TaskCard(
                                    task = task,
                                    subjectName = subj?.subject_name ?: "Sin materia",
                                    subjectColor = subj?.color ?: "#808080",
                                    onClick = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) },
                                    onAddClick = { navController.navigate(NavRoutes.EditTask.createRoute(task.id)) }
                                )
                            }
                        }
                    }
                } else {
                    items(filteredTasks) { task ->
                        val subj = viewModel.subjects.find { it.id == task.subject_id }
                        TaskCard(
                            task = task,
                            subjectName = subj?.subject_name ?: "Sin materia",
                            subjectColor = subj?.color ?: "#808080",
                            onClick = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) },
                            onAddClick = { navController.navigate(NavRoutes.EditTask.createRoute(task.id)) }
                        )
                    }
                }
            } else {
                items(viewModel.subjects) { subject ->
                    SubjectCard(
                        subject = subject,
                        onEditClick = { editingSubject = subject },
                        onDeleteClick = { viewModel.deleteSubject(subject.id) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
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
