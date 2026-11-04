package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = FreshGreen,
    onPrimary = ForestGreen,
    primaryContainer = ForestGreen,
    onPrimaryContainer = SoftMint,
    secondary = EmeraldGreen,
    onSecondary = PureWhite,
    secondaryContainer = SoftMintDark,
    onSecondaryContainer = FreshGreen,
    tertiary = SolarYellow,
    onTertiary = DarkCharcoal,
    background = DarkSurface,
    onBackground = WarmOffWhite,
    surface = DarkCardSurface,
    onSurface = WarmOffWhite,
    surfaceVariant = DarkCardBorder,
    onSurfaceVariant = WarmOffWhite.copy(alpha = 0.8f),
    outline = DarkCardBorder
)

val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = PureWhite,
    primaryContainer = SoftMint,
    onPrimaryContainer = ForestGreen,
    secondary = ForestGreen,
    onSecondary = PureWhite,
    secondaryContainer = SoftMint,
    onSecondaryContainer = ForestGreen,
    tertiary = SolarYellow,
    onTertiary = DarkCharcoal,
    background = WarmOffWhite,
    onBackground = DarkCharcoal,
    surface = PureWhite,
    onSurface = DarkCharcoal,
    surfaceVariant = SoftMint,
    onSurfaceVariant = ForestGreen,
    outline = EmeraldGreen.copy(alpha = 0.25f)
)

@Composable
fun EcoMindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
