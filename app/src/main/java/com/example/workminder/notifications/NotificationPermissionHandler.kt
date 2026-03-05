package com.example.workminder.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * NotificationPermissionHandler — Composable que solicita el permiso
 * POST_NOTIFICATIONS en Android 13+ (API 33+).
 *
 * Úsalo en MainActivity o en la primera pantalla donde se necesiten notificaciones.
 *
 * Uso:
 *   NotificationPermissionHandler()
 */
@Composable
fun NotificationPermissionHandler() {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* resultado — no es necesario manejar, el sistema recuerda la respuesta */ }

            LaunchedEffect(Unit) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
