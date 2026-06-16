package com.example.financas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceWhite,
    secondary = SecondaryTeal,
    onSecondary = SurfaceWhite,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    error = ErrorRed,
    onError = SurfaceWhite
)

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceWhite,
    secondary = SecondaryTeal,
    onSecondary = SurfaceWhite,
    background = TextPrimary,
    onBackground = BackgroundLight,
    surface = TextPrimary,
    onSurface = BackgroundLight,
    error = ErrorRed,
    onError = SurfaceWhite
)

@Composable
fun FinancasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}