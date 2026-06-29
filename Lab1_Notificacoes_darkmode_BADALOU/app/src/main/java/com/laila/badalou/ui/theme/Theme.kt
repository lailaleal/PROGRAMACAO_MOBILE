package com.laila.badalou.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Esquema de cores para o Tema Escuro
private val DarkColorScheme = darkColorScheme(
    primary = AmbarPrimary,
    onPrimary = Color.Black,
    primaryContainer = AmbarDark,
    onPrimaryContainer = Color.White,
    secondary = GreenSuccess,
    onSecondary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF3C3C3C),
    onSurfaceVariant = TextSecondaryDark
)

// Esquema de cores para o Tema Claro
private val LightColorScheme = lightColorScheme(
    primary = AmbarPrimary,
    onPrimary = Color.White,
    primaryContainer = AmbarLight,
    onPrimaryContainer = Color.Black,
    secondary = GreenSuccess,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = TextSecondary
)

@Composable
fun BadalouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BadalouTypography,
        content = content
    )
}