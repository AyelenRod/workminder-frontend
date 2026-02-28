package com.example.workminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.workminder.ui.navigation.NavGraph
import com.example.workminder.ui.theme.WorkMinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkMinderTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}