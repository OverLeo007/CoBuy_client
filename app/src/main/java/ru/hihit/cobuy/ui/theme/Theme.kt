package ru.hihit.cobuy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightText,

    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightText,

    secondary = LightSecondary,
    onSecondary = LightText,

    tertiary = LightPrimary,
    onTertiary = LightLightText,

    background = LightBackground,
    onBackground = LightText,

    surface = LightBackground,
    onSurface = LightText,

    surfaceTint = LightDivider
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkText,

    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkText,

    secondary = DarkSecondary,
    onSecondary = DarkText,

    tertiary = DarkPrimary,
    onTertiary = DarkLightText,

    background = DarkBackground,
    onBackground = DarkText,

    surface = DarkBackground,
    onSurface = DarkText,

    surfaceTint = DarkDivider
)

@Composable
fun CoBuyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}