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

// Default (Palette 4)
private val P4LightColorScheme = lightColorScheme(
    primary = P4_DarkBlue,
    onPrimary = P4_OffWhite,
    primaryContainer = P4_LightBlueGrey,
    onPrimaryContainer = P4_VeryDarkBlue,
    secondary = P4_Blue,
    onSecondary = P4_OffWhite,
    background = P4_OffWhite,
    onBackground = P4_VeryDarkBlue,
    surface = P4_OffWhite,
    onSurface = P4_VeryDarkBlue,
    surfaceVariant = P4_LightBlueGrey,
    onSurfaceVariant = P4_Blue
)

private val P4DarkColorScheme = darkColorScheme(
    primary = P4_LightBlueGrey,
    onPrimary = P4_VeryDarkBlue,
    primaryContainer = P4_DarkBlue,
    onPrimaryContainer = P4_OffWhite,
    secondary = P4_LightBlueGrey,
    onSecondary = P4_VeryDarkBlue,
    background = P4_VeryDarkBlue,
    onBackground = P4_OffWhite,
    surface = P4_Blue,
    onSurface = P4_OffWhite,
    surfaceVariant = P4_DarkBlue,
    onSurfaceVariant = P4_OffWhite
)

// Palette 1
private val P1LightColorScheme = lightColorScheme(
    primary = P1_Green,
    onPrimary = P1_DarkGrey,
    primaryContainer = P1_LightBlue,
    onPrimaryContainer = P1_DarkGrey,
    secondary = P1_GreyBlue,
    onSecondary = P1_White,
    background = P1_White,
    onBackground = P1_DarkGrey,
    surface = P1_White,
    onSurface = P1_DarkGrey,
    surfaceVariant = P1_LightBlue,
    onSurfaceVariant = P1_DarkGrey
)

private val P1DarkColorScheme = darkColorScheme(
    primary = P1_Green,
    onPrimary = P1_DarkGrey,
    primaryContainer = P1_GreyBlue,
    onPrimaryContainer = P1_White,
    secondary = P1_LightBlue,
    onSecondary = P1_DarkGrey,
    background = P1_DarkGrey,
    onBackground = P1_White,
    surface = P1_DarkGrey,
    onSurface = P1_White,
    surfaceVariant = P1_GreyBlue,
    onSurfaceVariant = P1_White
)

// Palette 2
private val P2LightColorScheme = lightColorScheme(
    primary = P2_Green,
    onPrimary = P2_LightGrey,
    primaryContainer = P2_Green,
    onPrimaryContainer = P2_Black,
    secondary = P2_DarkGreen,
    onSecondary = P2_LightGrey,
    background = P2_LightGrey,
    onBackground = P2_Black,
    surface = P2_LightGrey,
    onSurface = P2_Black,
    surfaceVariant = P2_Grey,
    onSurfaceVariant = P2_LightGrey
)

private val P2DarkColorScheme = darkColorScheme(
    primary = P2_Green,
    onPrimary = P2_Black,
    primaryContainer = P2_DarkGreen,
    onPrimaryContainer = P2_LightGrey,
    secondary = P2_Green,
    onSecondary = P2_Black,
    background = P2_Black,
    onBackground = P2_LightGrey,
    surface = P2_Grey,
    onSurface = P2_LightGrey,
    surfaceVariant = P2_DarkGreen,
    onSurfaceVariant = P2_LightGrey
)

// Palette 3
private val P3LightColorScheme = lightColorScheme(
    primary = P3_Green,
    onPrimary = P3_DarkGrey,
    primaryContainer = P3_LightGreen,
    onPrimaryContainer = P3_DarkGrey,
    secondary = P3_Orange,
    onSecondary = P3_LightYellow,
    background = P3_LightYellow,
    onBackground = P3_DarkGrey,
    surface = P3_LightYellow,
    onSurface = P3_DarkGrey,
    surfaceVariant = P3_LightGreen,
    onSurfaceVariant = P3_DarkGrey
)

private val P3DarkColorScheme = darkColorScheme(
    primary = P3_Green,
    onPrimary = P3_DarkGrey,
    primaryContainer = P3_Orange,
    onPrimaryContainer = P3_LightYellow,
    secondary = P3_LightGreen,
    onSecondary = P3_DarkGrey,
    background = P3_DarkGrey,
    onBackground = P3_LightYellow,
    surface = P3_DarkGrey,
    onSurface = P3_LightYellow,
    surfaceVariant = P3_Orange,
    onSurfaceVariant = P3_LightYellow
)

// Palette 5
private val P5LightColorScheme = lightColorScheme(
    primary = P5_Green,
    onPrimary = P5_LightGreen,
    primaryContainer = P5_LightGreen,
    onPrimaryContainer = P5_DarkBrown,
    secondary = P5_Orange,
    onSecondary = P5_DarkBrown,
    background = P5_LightGreen,
    onBackground = P5_DarkBrown,
    surface = P5_LightGreen,
    onSurface = P5_DarkBrown,
    surfaceVariant = P5_Orange,
    onSurfaceVariant = P5_DarkBrown,
    error = P5_Red,
    onError = P5_LightGreen
)

private val P5DarkColorScheme = darkColorScheme(
    primary = P5_Green,
    onPrimary = P5_DarkBrown,
    primaryContainer = P5_Orange,
    onPrimaryContainer = P5_DarkBrown,
    secondary = P5_Red,
    onSecondary = P5_DarkBrown,
    background = P5_DarkBrown,
    onBackground = P5_LightGreen,
    surface = P5_DarkBrown,
    onSurface = P5_LightGreen,
    surfaceVariant = P5_Orange,
    onSurfaceVariant = P5_DarkBrown,
    error = P5_Red,
    onError = P5_DarkBrown
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    appTheme: AppThemePalette = AppThemePalette.PALETTE_4,
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
