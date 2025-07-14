package com.bestamibakir.rentagri.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = EarthGreen70,
    onPrimary = OnSurfaceDark,
    primaryContainer = EarthGreen20,
    onPrimaryContainer = EarthGreen80,
    secondary = WarmBrown70,
    onSecondary = OnSurfaceDark,
    secondaryContainer = WarmBrown20,
    onSecondaryContainer = WarmBrown80,
    tertiary = GoldenYellow70,
    onTertiary = OnSurfaceLight,
    tertiaryContainer = GoldenYellow20,
    onTertiaryContainer = GoldenYellow80,
    error = ErrorRed,
    errorContainer = Color(0xFF93000A),
    onError = Color.White,
    onErrorContainer = Color(0xFFFFDAD6),
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = WarmBrown40,
    onSurfaceVariant = WarmBrown80,
    outline = WarmBrown60,
    inverseOnSurface = OnSurfaceLight,
    inverseSurface = SurfaceLight,
    inversePrimary = EarthGreen40,
    surfaceTint = EarthGreen70
)

private val LightColorScheme = lightColorScheme(
    primary = EarthGreen60,
    onPrimary = Color.White,
    primaryContainer = EarthGreen80,
    onPrimaryContainer = EarthGreen20,
    secondary = WarmBrown60,
    onSecondary = Color.White,
    secondaryContainer = WarmBrown80,
    onSecondaryContainer = WarmBrown20,
    tertiary = GoldenYellow60,
    onTertiary = OnSurfaceLight,
    tertiaryContainer = GoldenYellow80,
    onTertiaryContainer = GoldenYellow20,
    error = ErrorRed,
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF93000A),
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = LightBeige,
    onSurfaceVariant = WarmBrown40,
    outline = WarmBrown40,
    inverseOnSurface = OnSurfaceDark,
    inverseSurface = SurfaceDark,
    inversePrimary = EarthGreen80,
    surfaceTint = EarthGreen60
)

@Composable
fun RentAgriTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}