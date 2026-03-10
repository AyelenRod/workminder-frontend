package com.example.workminder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.*
import com.example.workminder.ui.components.BottomNavBar
import com.example.workminder.ui.components.TaskCard
import com.example.workminder.ui.components.WorkMinderTopBar
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workminder.ui.viewmodel.MainViewModel
import com.example.workminder.data.model.TaskStatus

@Composable
fun DashboardScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    val now = java.time.LocalDate.now()
    val in7Days = now.plusDays(7)

    val pendingThisWeek = viewModel.tasks.count { task ->
        if (task.status != com.example.workminder.data.model.TaskStatus.PENDING) return@count false
        try {
            val date = java.time.LocalDate.parse(task.due_date.split("T")[0])
            !date.isBefore(now) && !date.isAfter(in7Days)
        } catch (e: Exception) { false }
    }

    val lateCount = viewModel.tasks.count { it.status == com.example.workminder.data.model.TaskStatus.LATE }
    
    val mostUrgentTasks = viewModel.tasks
        .filter { it.status == com.example.workminder.data.model.TaskStatus.PENDING || it.status == com.example.workminder.data.model.TaskStatus.LATE }
        .sortedByDescending { it.urgency }
        .take(3)

    Scaffold(
        topBar = {
            WorkMinderTopBar(
                subtitle = "Hola de nuevo,",
                name = viewModel.userName,
                onSettingsClick = { navController.navigate(NavRoutes.Settings.route) }
            )
        },
        bottomBar = { BottomNavBar(navController = navController) },
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

            if (viewModel.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = YellowPrimary)
            }

            // Stats cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Pendientes esta semana",
                    count = pendingThisWeek,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Total atrasadas",
                    count = lateCount,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Más urgentes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NavyText
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (mostUrgentTasks.isEmpty() && !viewModel.isLoading) {
                Text("No tienes tareas urgentes. ¡Buen trabajo!", color = TextSecondary)
            }

            mostUrgentTasks.forEach { task ->
                val subj = viewModel.subjects.find { it.id == task.subject_id }
                TaskCard(
                    task = task,
                    subjectName = subj?.subject_name ?: "Sin materia",
                    subjectColor = subj?.color ?: "#808080",
                    onClick = { navController.navigate(NavRoutes.TaskDetail.createRoute(task.id)) },
                    onAddClick = { navController.navigate(NavRoutes.EditTask.createRoute(task.id)) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatCard(label: String, count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = NavyText
            )
        }
    }
}
