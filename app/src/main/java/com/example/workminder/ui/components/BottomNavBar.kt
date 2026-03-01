package com.example.workminder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.workminder.ui.navigation.NavRoutes
import com.example.workminder.ui.theme.NavyText
import com.example.workminder.ui.theme.YellowPrimary

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        color = YellowPrimary,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(60.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                navController.navigate(NavRoutes.Dashboard.route) {
                    popUpTo(NavRoutes.Dashboard.route) { inclusive = true }
                    launchSingleTop = true
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Inicio",
                    tint = NavyText.copy(alpha = if (currentRoute == NavRoutes.Dashboard.route) 1f else 0.5f),
                    modifier = Modifier.size(28.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (currentRoute == NavRoutes.NewTask.route) NavyText.copy(alpha = 0.12f) else NavyText.copy(alpha = 0f)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { navController.navigate(NavRoutes.NewTask.route) }) {
                    Icon(
                        imageVector = Icons.Filled.NoteAdd,
                        contentDescription = "Nueva tarea",
                        tint = NavyText.copy(alpha = if (currentRoute == NavRoutes.NewTask.route) 1f else 0.5f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            IconButton(onClick = {
                navController.navigate(NavRoutes.Agenda.route) {
                    launchSingleTop = true
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.EventNote,
                    contentDescription = "Agenda",
                    tint = NavyText.copy(alpha = if (currentRoute == NavRoutes.Agenda.route) 1f else 0.5f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
