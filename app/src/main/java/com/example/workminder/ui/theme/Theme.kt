package com.example.workminder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val WorkMinderColorScheme = lightColorScheme(
    primary            = YellowPrimary,
    onPrimary          = NavyText,
    primaryContainer   = YellowDark,
    onPrimaryContainer = NavyText,
    secondary          = NavyText,
    onSecondary        = SurfaceWhite,
    background         = BackgroundGray,
    onBackground       = NavyText,
    surface            = SurfaceWhite,
    onSurface          = NavyText,
    surfaceVariant     = BackgroundGray,
    onSurfaceVariant   = TextSecondary,
    tertiary           = SaveGreen,
    onTertiary         = SurfaceWhite,
    error              = UrgentRed,
    onError            = SurfaceWhite,
)

@Composable
fun WorkMinderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WorkMinderColorScheme,
        typography  = Typography,
        content     = content
    )
}
