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
import com.example.workminder.data.model.MockData
import com.example.workminder.ui.components.BottomNavBar
import com.example.workminder.ui.components.TaskCard
import com.example.workminder.ui.components.WorkMinderTopBar
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.*

@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            WorkMinderTopBar(
                subtitle = "Hola de nuevo,",
                name = MockData.userName,
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

            // Stats cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Tareas pendientes",
                    count = MockData.pendingCount,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Tareas atrasadas",
                    count = MockData.lateCount,
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

            MockData.suggestedTasks.forEach { task ->
                TaskCard(
                    task = task,
                    onClick = {
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
