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
import com.example.workminder.data.model.Task
import com.example.workminder.data.model.TaskStatus

@Composable
fun AgendaScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    var showFilter by remember { mutableStateOf(false) }
<<<<<<< Updated upstream
    var activeStatusFilter by remember { mutableStateOf("Por hacer") }
    var activeSortBy by remember { mutableStateOf("Fecha de entrega") }

    var thisWeekExpanded by remember { mutableStateOf(true) }
    var nextWeekExpanded by remember { mutableStateOf(false) }
    var laterExpanded by remember { mutableStateOf(false) }

    var viewMode by remember { mutableStateOf("Tareas") }

    var showAddDialog by remember { mutableStateOf(false) }
    var showNewSubjectDialog by remember { mutableStateOf(false) }
    var showNoSubjectsWarning by remember { mutableStateOf(false) }
=======
    var activeStatusFilter by remember { mutableStateOf("Todos") }
    var activeSortBy by remember { mutableStateOf("Fecha") }
    var viewMode by remember { mutableStateOf("Tareas") }
>>>>>>> Stashed changes

    var showAddDialog by remember { mutableStateOf(false) }
    var showNewSubjectDialog by remember { mutableStateOf(false) }
    var showNoSubjectsWarning by remember { mutableStateOf(false) }

    var thisWeekExpanded by remember { mutableStateOf(true) }
    var nextWeekExpanded by remember { mutableStateOf(true) }
    var laterExpanded by remember { mutableStateOf(true) }

    var editingSubject by remember { mutableStateOf<Subject?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    val filteredTasks by remember(query, activeStatusFilter, activeSortBy) {
        derivedStateOf {
            var list: List<Task> = viewModel.tasks.toList()

            if (query.isNotBlank()) {
                list = list.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.notes.contains(query, ignoreCase = true)
                }
            }
//TaskCard
            list = when (activeStatusFilter) {
                "Por hacer"  -> list.filter {
                    it.status == TaskStatus.PENDING ||
                    it.status == TaskStatus.IN_PROGRESS ||
                    it.status == TaskStatus.LATE
                }
                "Atrasadas"  -> list.filter { it.status == TaskStatus.LATE }
                "Terminadas" -> list.filter { it.status == TaskStatus.DONE }
                else -> list
            }

            list = when (activeSortBy) {
                "Importancia"       -> list.sortedByDescending { it.urgency }
                "Complejidad"       -> list.sortedByDescending { it.complexity }
                "Materia"           -> list.sortedBy { it.subject_id }
                else                -> list.sortedBy { it.due_date }
            }
            list
        }
    }

    if (showFilter) {
        FilterDialog(
            onDismiss = { showFilter = false },
            onApply   = { sortBy, filter ->
                activeSortBy       = sortBy
                activeStatusFilter = filter
                showFilter         = false
            }
        )
    }

    if (showNoSubjectsWarning) {
        WorkMinderDialog(
            onDismissRequest = { showNoSubjectsWarning = false },
            title = "Atención",
            message = "Debes tener registrada por lo menos una materia antes de agregar una tarea.",
            confirmText = "Entendido",
            onConfirm = { showNoSubjectsWarning = false }
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
                    Text(
                        text = "¿Qué deseas agregar?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyText
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            showAddDialog = false
                            if (viewModel.subjects.isEmpty()) {
                                showNewSubjectDialog = true // Directo a crear materia
                            } else {
                                navController.navigate(NavRoutes.NewTask.route)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary, contentColor = NavyText)
                    ) {
                        Text("Nueva tarea", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            showAddDialog = false
                            showNewSubjectDialog = true
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyText)
                    ) {
                        Text("Nueva materia", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            WorkMinderTopBar(
                subtitle = "La Agenda de",
                name = "Usuario",
                onSettingsClick = { navController.navigate(NavRoutes.Settings.route) },
                subtitle = "La Agenda de"
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = YellowPrimary,
                contentColor = NavyText,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
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
                        placeholder = { Text("Buscar...", color = TextSecondary) },
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
                    
                    if (viewMode == "Tareas") {
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
                }
                Spacer(modifier = Modifier.height(16.dp))

                // View Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewMode = "Tareas" },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (viewMode == "Tareas") YellowPrimary else TextSecondary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (viewMode == "Tareas") YellowPrimary.copy(alpha=0.1f) else Color.Transparent,
                            contentColor = if (viewMode == "Tareas") NavyText else TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tareas", fontWeight = if (viewMode == "Tareas") FontWeight.Bold else FontWeight.Normal)
                    }
                    OutlinedButton(
                        onClick = { viewMode = "Materias" },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (viewMode == "Materias") YellowPrimary else TextSecondary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (viewMode == "Materias") YellowPrimary.copy(alpha=0.1f) else Color.Transparent,
                            contentColor = if (viewMode == "Materias") NavyText else TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Materias", fontWeight = if (viewMode == "Materias") FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            if (viewMode == "Tareas") {
                if (query.isNotBlank() || activeStatusFilter != "Todos") {
                    // Modo búsqueda/filtro: lista plana sin agrupar
                    item {
                        if (filteredTasks.isEmpty()) {
                            Text(
                                "No se encontraron tareas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }
                    }
                    items(filteredTasks) { task ->
                        val subj = viewModel.subjects.find { it.id == task.subject_id }
                        TaskCard(
                            task = task,
                            subjectName = subj?.subject_name ?: "Sin materia",
                            subjectColor = subj?.color ?: "#808080",
                            modifier = Modifier.padding(bottom = 8.dp),
                            onClick = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) },
                            onAddClick = { navController.navigate(NavRoutes.EditTask.createRoute(task.id)) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                } else {
                    // Modo normal agrupado
                    val thisWeek  = filteredTasks.take(3)
                    val nextWeek  = filteredTasks.drop(3).take(2)
                    val laterList = filteredTasks.drop(5)

                    // Esta semana
                    item {
                        TaskGroup(
                            label    = "Esta semana",
                            count    = thisWeek.size,
                            expanded = thisWeekExpanded,
                            onToggle = { thisWeekExpanded = !thisWeekExpanded }
                        )
                    }
                    item {
                        AnimatedVisibility(visible = thisWeekExpanded) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Spacer(modifier = Modifier.height(4.dp))
                                for (task in thisWeek) {
                                    val subj = viewModel.subjects.find { it.id == task.subject_id }
                                    TaskCard(
                                        task = task,
                                        subjectName = subj?.subject_name ?: "Sin materia",
                                        subjectColor = subj?.color ?: "#808080",
                                        onClick = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) },
                                        onAddClick = { navController.navigate(NavRoutes.EditTask.createRoute(task.id)) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    // Siguiente semana
                    item {
                        TaskGroup(
                            label    = "Siguiente semana",
                            count    = nextWeek.size,
                            expanded = nextWeekExpanded,
                            onToggle = { nextWeekExpanded = !nextWeekExpanded }
                        )
                    }
                    item {
                        AnimatedVisibility(visible = nextWeekExpanded) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Spacer(modifier = Modifier.height(4.dp))
                                for (task in nextWeek) {
                                    val subj = viewModel.subjects.find { it.id == task.subject_id }
                                    TaskCard(
                                        task = task,
                                        subjectName = subj?.subject_name ?: "Sin materia",
                                        subjectColor = subj?.color ?: "#808080",
                                        onClick = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) },
                                        onAddClick = { navController.navigate(NavRoutes.EditTask.createRoute(task.id)) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    // Más tarde
                    item {
                        TaskGroup(
                            label    = "Más tarde",
                            count    = laterList.size,
                            expanded = laterExpanded,
                            onToggle = { laterExpanded = !laterExpanded }
                        )
                    }
                    item {
                        AnimatedVisibility(visible = laterExpanded) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Spacer(modifier = Modifier.height(4.dp))
                                for (task in laterList) {
                                    val subj = viewModel.subjects.find { it.id == task.subject_id }
                                    TaskCard(
                                        task = task,
                                        subjectName = subj?.subject_name ?: "Sin materia",
                                        subjectColor = subj?.color ?: "#808080",
                                        modifier = Modifier.padding(bottom = 10.dp),
                                        onClick = {
                                            navController.navigate(NavRoutes.TaskDetail.createRoute(task.id))
                                        },
                                        onAddClick = { navController.navigate(NavRoutes.EditTask.createRoute(task.id)) }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                // Materias Mode
                item {
                    Text(
                        text = "Mis Materias",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyText,
                        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                    )
                }
                items(viewModel.subjects) { subject ->
                    SubjectCard(
                        subject = subject,
                        modifier = Modifier.padding(bottom = 10.dp),
                        onEditClick = {
                            editingSubject = subject
                        },
                        onDeleteClick = {
                            viewModel.deleteSubject(subject.id)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
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
