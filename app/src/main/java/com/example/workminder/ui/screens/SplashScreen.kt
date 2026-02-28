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
import androidx.compose.ui.unit.dp
import com.example.workminder.ui.theme.NavyText
import com.example.workminder.ui.theme.YellowPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000L)
        onSplashFinished()
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
            // Logo: pencil + apple approximation
            Box(modifier = Modifier.size(110.dp), contentAlignment = Alignment.Center) {
                // Apple (circle behind)
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .offset(x = 18.dp, y = 6.dp)
                        .clip(CircleShape)
                        .background(NavyText)
                )
                // Pencil (icon in front-left)
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = NavyText,
                    modifier = Modifier
                        .size(66.dp)
                        .offset(x = (-14).dp, y = 4.dp)
                )
            }

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
