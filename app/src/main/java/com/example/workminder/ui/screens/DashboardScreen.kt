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
    // Al entrar a la pantalla, refrescamos datos
    LaunchedEffect(Unit) {
        viewModel.refreshAll()
    }

    val activeTasks = viewModel.tasks.filter { it.status != TaskStatus.DONE }
    val pendingCount = activeTasks.count { it.status == TaskStatus.PENDING || it.status == TaskStatus.IN_PROGRESS }
    val lateCount = activeTasks.count { it.status == TaskStatus.LATE }

    Scaffold(
        topBar = {
            WorkMinderTopBar(
                subtitle = "Hola de nuevo,",
                name = "Usuario", // TODO: Obtener nombre real del perfil
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
                    label = "Tareas pendientes",
                    count = pendingCount,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Tareas atrasadas",
                    count = lateCount,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Considera trabajar en...",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NavyText
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (viewModel.tasks.isEmpty() && !viewModel.isLoading) {
                Text("No tienes tareas pendientes. ¡Buen trabajo!", color = TextSecondary)
            }

            activeTasks.take(5).forEach { task ->
                val subj = viewModel.subjects.find { it.id == task.subject_id }
                TaskCard(
                    task = task,
                    subjectName = subj?.subject_name ?: "Sin materia",
                    subjectColor = subj?.color ?: "#808080",
                    onClick = {
                        navController.navigate(NavRoutes.TaskDetail.createRoute(task.id))
                    },
                    onAddClick = {
                        navController.navigate(NavRoutes.TaskDetail.createRoute(task.id))
                    }
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
