package com.myworkoutroutine.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = FitnessRed,
    onPrimary = OffWhite,
    primaryContainer = FitnessRedDark,
    onPrimaryContainer = OffWhite,

    secondary = CarbonFiberLight,
    onSecondary = OffWhite,
    secondaryContainer = DarkGray,
    onSecondaryContainer = OffWhite,

    tertiary = MediumGray,
    onTertiary = OffWhite,

    background = CarbonFiber,
    onBackground = OffWhite,

    surface = CarbonFiberLight,
    onSurface = OffWhite,
    surfaceVariant = DarkGray,
    onSurfaceVariant = LightGray,

    error = FitnessRedLight,
    onError = OffWhite,

    outline = MediumGray,
    outlineVariant = DarkGray
)

@Composable
fun MyWorkoutRoutineTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
