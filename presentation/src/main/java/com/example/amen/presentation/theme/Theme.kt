package com.example.amen.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Hallow 앱 벤치마킹 - 어두운 영성 앱 컨셉이므로 기본적으로 Dark Theme을 강제/우대함
private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = GoldLight,
    background = NavyDark,
    surface = NavyLight,
    onPrimary = NavyDark,
    onSecondary = NavyDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    secondary = GoldLight,
    background = NavyDark,  // Light 테마에서도 고유의 분위기를 위해 어두운 배경 유지 (필요에 따라 수정)
    surface = NavyLight,
    onPrimary = NavyDark,
    onSecondary = NavyDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun AmenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+, but we disable it to keep the app's unique identity
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // dynamicTheme -> ...
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // 아멘 앱은 컨셉상 다크모드를 기본으로 함
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
