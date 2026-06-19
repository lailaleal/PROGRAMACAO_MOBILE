package com.example.financas.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private val SaldoDarkColors = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = BackgroundDark,
    secondary = GoldLight,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed,
    onError = TextPrimaryDark,
    outline = DividerDark
)

@Composable
fun FinancasTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SaldoDarkColors,
        typography = Typography
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            content()
        }
    }
}