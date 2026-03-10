package com.example.workminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.workminder.notifications.NotificationPermissionHandler
import com.example.workminder.ui.navigation.NavGraph
import com.example.workminder.ui.theme.WorkMinderTheme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.workminder.data.remote.AuthManager.init(this)
        com.example.workminder.data.remote.NetworkConfig.init(this)
        enableEdgeToEdge()
        
        // Buscar el servidor automáticamente en segundo plano
        lifecycleScope.launch {
            com.example.workminder.data.remote.NetworkConfig.discoverServer(this@MainActivity)
        }

        setContent {
            WorkMinderTheme {
                // Solicita permiso de notificaciones en Android 13+
                NotificationPermissionHandler()
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}