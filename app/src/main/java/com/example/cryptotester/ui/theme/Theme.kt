package com.example.cryptotester.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = DarkBackground,
    primaryContainer = AccentBlue.copy(alpha = 0.15f),
    onPrimaryContainer = AccentBlue,
    secondary = AccentPurple,
    onSecondary = DarkBackground,
    secondaryContainer = AccentPurple.copy(alpha = 0.15f),
    onSecondaryContainer = AccentPurple,
    tertiary = CryptoGreen,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
    outlineVariant = DarkSurfaceVariant,
    error = CryptoRed,
    onError = DarkBackground,
)

@Composable
fun CryptoTesterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}