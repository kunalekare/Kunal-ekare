package com.example.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val VidyaDarkColorScheme = darkColorScheme(
    primary = VidyaBlueLight,
    onPrimary = Slate950,
    primaryContainer = VidyaNavy,
    onPrimaryContainer = Slate100,
    secondary = VidyaAmberLight,
    onSecondary = Slate950,
    secondaryContainer = Slate800,
    onSecondaryContainer = VidyaAmberLight,
    tertiary = VidyaEmeraldLight,
    onTertiary = Slate950,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    outline = Slate700,
    error = VidyaCrimson
)

private val VidyaLightColorScheme = lightColorScheme(
    primary = VidyaBluePrimary,
    onPrimary = White,
    primaryContainer = Slate100,
    onPrimaryContainer = VidyaNavyDark,
    secondary = VidyaAmberSecondary,
    onSecondary = White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = VidyaAmberDark,
    tertiary = VidyaEmeraldTertiary,
    onTertiary = White,
    background = Slate50,
    onBackground = Slate900,
    surface = White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate300,
    error = VidyaCrimson
)

@Composable
fun VidyaSetuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> VidyaDarkColorScheme
        else -> VidyaLightColorScheme
    }

    CompositionLocalProvider(
        LocalSpacing provides VidyaSpacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = VidyaTypography,
            content = content
        )
    }
}
