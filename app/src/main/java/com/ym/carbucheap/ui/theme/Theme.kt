package com.ym.carbucheap.ui.theme

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
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green700,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Green100,
    onPrimaryContainer = Green900,
    secondary = Green500,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Green50,
    onSecondaryContainer = Green900,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    error = Red500,
    onError = androidx.compose.ui.graphics.Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Green200Dark,
    onPrimary = Green900,
    primaryContainer = Green700Dark,
    onPrimaryContainer = Green100,
    secondary = Green200Dark,
    onSecondary = Green900,
    secondaryContainer = Green700Dark,
    onSecondaryContainer = Green100,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    error = Red500,
    onError = androidx.compose.ui.graphics.Color.White
)

@Composable
fun CarbuCheapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
