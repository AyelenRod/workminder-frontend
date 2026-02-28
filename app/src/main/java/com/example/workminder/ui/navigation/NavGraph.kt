package com.example.workminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.workminder.ui.screens.AgendaScreen
import com.example.workminder.ui.screens.DashboardScreen
import com.example.workminder.ui.screens.EditTaskScreen
import com.example.workminder.ui.screens.LoginScreen
import com.example.workminder.ui.screens.NewTaskScreen
import com.example.workminder.ui.screens.RegisterScreen
import com.example.workminder.ui.screens.SettingsScreen
import com.example.workminder.ui.screens.SplashScreen
import com.example.workminder.ui.screens.TaskDetailScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController   = navController,
        startDestination = NavRoutes.Splash.route
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(NavRoutes.Login.route) {
                    popUpTo(NavRoutes.Splash.route) { inclusive = true }
                }
            })
        }
        composable(NavRoutes.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(NavRoutes.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(NavRoutes.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(NavRoutes.Agenda.route) {
            AgendaScreen(navController = navController)
        }
        composable(NavRoutes.NewTask.route) {
            NewTaskScreen(navController = navController)
        }
        composable(
            route = NavRoutes.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            TaskDetailScreen(taskId = taskId, navController = navController)
        }
        composable(
            route = NavRoutes.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: 0
            EditTaskScreen(taskId = taskId, navController = navController)
        }
    }
}
