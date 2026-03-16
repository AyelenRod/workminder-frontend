package com.example.workminder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.workminder.R
import androidx.compose.ui.unit.dp
import com.example.workminder.ui.theme.NavyText
import com.example.workminder.ui.theme.YellowPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: (Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000L)
        onSplashFinished(com.example.workminder.data.remote.AuthManager.isLoggedIn())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YellowPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.ic_workminder_logo),
                contentDescription = "WorkMinder Logo",
                modifier = Modifier.size(110.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "WORKMINDER!!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = NavyText
            )
        }
    }
}
