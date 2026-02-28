package com.example.workminder.ui.navigation

sealed class NavRoutes(val route: String) {
    object Splash     : NavRoutes("splash")
    object Login      : NavRoutes("login")
    object Register   : NavRoutes("register")
    object Settings   : NavRoutes("settings")
    object Dashboard  : NavRoutes("dashboard")
    object Agenda     : NavRoutes("agenda")
    object NewTask    : NavRoutes("new_task")
    object TaskDetail : NavRoutes("task_detail/{taskId}") {
        fun createRoute(taskId: Int) = "task_detail/$taskId"
    }
    object EditTask   : NavRoutes("edit_task/{taskId}") {
        fun createRoute(taskId: Int) = "edit_task/$taskId"
    }
}
