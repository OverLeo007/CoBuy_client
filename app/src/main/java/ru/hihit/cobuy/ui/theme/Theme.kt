package ru.hihit.cobuy.ui.theme

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import me.zhanghai.compose.preference.Preferences
import ru.hihit.cobuy.ui.components.viewmodels.SettingKeys

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

    surfaceTint = LightDivider,

    error = LightError,

    surfaceBright = LightInfo
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

    surfaceTint = DarkDivider,

    error = DarkError,

    surfaceBright = DarkInfo
)



@Composable
fun CoBuyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    preferenceFlow: MutableStateFlow<Preferences>? = null,
    content: @Composable () -> Unit
) {
    val preferences = preferenceFlow?.collectAsStateWithLifecycle()
    val themePreference = preferences?.value?.asMap()?.getOrDefault(SettingKeys.THEME, SettingKeys.THEME_SYSTEM)

    preferences?.value?.let {
        Log.d("CoBuyTheme", "Preferences updated ${it.asMap().entries}")
    }

    val colorScheme = when (themePreference) {
        SettingKeys.THEME_LIGHT -> LightColorScheme
        SettingKeys.THEME_DARK -> DarkColorScheme
        SettingKeys.THEME_SYSTEM -> if (darkTheme) DarkColorScheme else LightColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
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