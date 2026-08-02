package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp)
)

enum class AppThemePalette {
    PALETTE_1, PALETTE_2, PALETTE_3, PALETTE_4, PALETTE_5
}

// Palette 1
private val P1LightColorScheme = lightColorScheme(
    primary = LavenderLightPrimary, onPrimary = LavenderLightOnPrimary,
    primaryContainer = LavenderLightPrimaryContainer, onPrimaryContainer = LavenderLightOnPrimaryContainer,
    secondary = LavenderLightSecondary, onSecondary = LavenderLightOnSecondary,
    secondaryContainer = LavenderLightSecondaryContainer, onSecondaryContainer = LavenderLightOnSecondaryContainer,
    background = LavenderLightBackground, onBackground = LavenderLightOnBackground,
    surface = LavenderLightSurface, onSurface = LavenderLightOnSurface,
    surfaceVariant = LavenderLightSurfaceVariant, onSurfaceVariant = LavenderLightOnSurfaceVariant
)
private val P1DarkColorScheme = darkColorScheme(
    primary = LavenderDarkPrimary, onPrimary = LavenderDarkOnPrimary,
    primaryContainer = LavenderDarkPrimaryContainer, onPrimaryContainer = LavenderDarkOnPrimaryContainer,
    secondary = LavenderDarkSecondary, onSecondary = LavenderDarkOnSecondary,
    secondaryContainer = LavenderDarkSecondaryContainer, onSecondaryContainer = LavenderDarkOnSecondaryContainer,
    background = LavenderDarkBackground, onBackground = LavenderDarkOnBackground,
    surface = LavenderDarkSurface, onSurface = LavenderDarkOnSurface,
    surfaceVariant = LavenderDarkSurfaceVariant, onSurfaceVariant = LavenderDarkOnSurfaceVariant
)

// Palette 2
private val P2LightColorScheme = lightColorScheme(
    primary = MintLightPrimary, onPrimary = MintLightOnPrimary,
    primaryContainer = MintLightPrimaryContainer, onPrimaryContainer = MintLightOnPrimaryContainer,
    secondary = MintLightSecondary, onSecondary = MintLightOnSecondary,
    secondaryContainer = MintLightSecondaryContainer, onSecondaryContainer = MintLightOnSecondaryContainer,
    background = MintLightBackground, onBackground = MintLightOnBackground,
    surface = MintLightSurface, onSurface = MintLightOnSurface,
    surfaceVariant = MintLightSurfaceVariant, onSurfaceVariant = MintLightOnSurfaceVariant
)
private val P2DarkColorScheme = darkColorScheme(
    primary = MintDarkPrimary, onPrimary = MintDarkOnPrimary,
    primaryContainer = MintDarkPrimaryContainer, onPrimaryContainer = MintDarkOnPrimaryContainer,
    secondary = MintDarkSecondary, onSecondary = MintDarkOnSecondary,
    secondaryContainer = MintDarkSecondaryContainer, onSecondaryContainer = MintDarkOnSecondaryContainer,
    background = MintDarkBackground, onBackground = MintDarkOnBackground,
    surface = MintDarkSurface, onSurface = MintDarkOnSurface,
    surfaceVariant = MintDarkSurfaceVariant, onSurfaceVariant = MintDarkOnSurfaceVariant
)

// Palette 3
private val P3LightColorScheme = lightColorScheme(
    primary = PeachLightPrimary, onPrimary = PeachLightOnPrimary,
    primaryContainer = PeachLightPrimaryContainer, onPrimaryContainer = PeachLightOnPrimaryContainer,
    secondary = PeachLightSecondary, onSecondary = PeachLightOnSecondary,
    secondaryContainer = PeachLightSecondaryContainer, onSecondaryContainer = PeachLightOnSecondaryContainer,
    background = PeachLightBackground, onBackground = PeachLightOnBackground,
    surface = PeachLightSurface, onSurface = PeachLightOnSurface,
    surfaceVariant = PeachLightSurfaceVariant, onSurfaceVariant = PeachLightOnSurfaceVariant
)
private val P3DarkColorScheme = darkColorScheme(
    primary = PeachDarkPrimary, onPrimary = PeachDarkOnPrimary,
    primaryContainer = PeachDarkPrimaryContainer, onPrimaryContainer = PeachDarkOnPrimaryContainer,
    secondary = PeachDarkSecondary, onSecondary = PeachDarkOnSecondary,
    secondaryContainer = PeachDarkSecondaryContainer, onSecondaryContainer = PeachDarkOnSecondaryContainer,
    background = PeachDarkBackground, onBackground = PeachDarkOnBackground,
    surface = PeachDarkSurface, onSurface = PeachDarkOnSurface,
    surfaceVariant = PeachDarkSurfaceVariant, onSurfaceVariant = PeachDarkOnSurfaceVariant
)

// Palette 4
private val P4LightColorScheme = lightColorScheme(
    primary = OceanLightPrimary, onPrimary = OceanLightOnPrimary,
    primaryContainer = OceanLightPrimaryContainer, onPrimaryContainer = OceanLightOnPrimaryContainer,
    secondary = OceanLightSecondary, onSecondary = OceanLightOnSecondary,
    secondaryContainer = OceanLightSecondaryContainer, onSecondaryContainer = OceanLightOnSecondaryContainer,
    background = OceanLightBackground, onBackground = OceanLightOnBackground,
    surface = OceanLightSurface, onSurface = OceanLightOnSurface,
    surfaceVariant = OceanLightSurfaceVariant, onSurfaceVariant = OceanLightOnSurfaceVariant
)
private val P4DarkColorScheme = darkColorScheme(
    primary = OceanDarkPrimary, onPrimary = OceanDarkOnPrimary,
    primaryContainer = OceanDarkPrimaryContainer, onPrimaryContainer = OceanDarkOnPrimaryContainer,
    secondary = OceanDarkSecondary, onSecondary = OceanDarkOnSecondary,
    secondaryContainer = OceanDarkSecondaryContainer, onSecondaryContainer = OceanDarkOnSecondaryContainer,
    background = OceanDarkBackground, onBackground = OceanDarkOnBackground,
    surface = OceanDarkSurface, onSurface = OceanDarkOnSurface,
    surfaceVariant = OceanDarkSurfaceVariant, onSurfaceVariant = OceanDarkOnSurfaceVariant
)

// Palette 5
private val P5LightColorScheme = lightColorScheme(
    primary = RoseLightPrimary, onPrimary = RoseLightOnPrimary,
    primaryContainer = RoseLightPrimaryContainer, onPrimaryContainer = RoseLightOnPrimaryContainer,
    secondary = RoseLightSecondary, onSecondary = RoseLightOnSecondary,
    secondaryContainer = RoseLightSecondaryContainer, onSecondaryContainer = RoseLightOnSecondaryContainer,
    background = RoseLightBackground, onBackground = RoseLightOnBackground,
    surface = RoseLightSurface, onSurface = RoseLightOnSurface,
    surfaceVariant = RoseLightSurfaceVariant, onSurfaceVariant = RoseLightOnSurfaceVariant
)
private val P5DarkColorScheme = darkColorScheme(
    primary = RoseDarkPrimary, onPrimary = RoseDarkOnPrimary,
    primaryContainer = RoseDarkPrimaryContainer, onPrimaryContainer = RoseDarkOnPrimaryContainer,
    secondary = RoseDarkSecondary, onSecondary = RoseDarkOnSecondary,
    secondaryContainer = RoseDarkSecondaryContainer, onSecondaryContainer = RoseDarkOnSecondaryContainer,
    background = RoseDarkBackground, onBackground = RoseDarkOnBackground,
    surface = RoseDarkSurface, onSurface = RoseDarkOnSurface,
    surfaceVariant = RoseDarkSurfaceVariant, onSurfaceVariant = RoseDarkOnSurfaceVariant
)


@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    appTheme: AppThemePalette = AppThemePalette.PALETTE_1,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> when (appTheme) {
            AppThemePalette.PALETTE_1 -> if (darkTheme) P1DarkColorScheme else P1LightColorScheme
            AppThemePalette.PALETTE_2 -> if (darkTheme) P2DarkColorScheme else P2LightColorScheme
            AppThemePalette.PALETTE_3 -> if (darkTheme) P3DarkColorScheme else P3LightColorScheme
            AppThemePalette.PALETTE_4 -> if (darkTheme) P4DarkColorScheme else P4LightColorScheme
            AppThemePalette.PALETTE_5 -> if (darkTheme) P5DarkColorScheme else P5LightColorScheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
