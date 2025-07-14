package com.bestamibakir.rentagri.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


val EarthGreen80 = Color(0xFF8BC34A)
val EarthGreen70 = Color(0xFF7CB342)
val EarthGreen60 = Color(0xFF689F38)
val EarthGreen40 = Color(0xFF558B2F)
val EarthGreen20 = Color(0xFF33691E)


val WarmBrown80 = Color(0xFFBCAAA4)
val WarmBrown70 = Color(0xFFA1887F)
val WarmBrown60 = Color(0xFF8D6E63)
val WarmBrown40 = Color(0xFF5D4037)
val WarmBrown20 = Color(0xFF3E2723)


val GoldenYellow80 = Color(0xFFFFF176)
val GoldenYellow70 = Color(0xFFFFEE58)
val GoldenYellow60 = Color(0xFFFFEB3B)
val GoldenYellow40 = Color(0xFFFBC02D)
val GoldenYellow20 = Color(0xFFF57F17)


val SoftCream = Color(0xFFFFFBF0)
val LightBeige = Color(0xFFF5F5DC)
val DeepForest = Color(0xFF2E7D32)
val SunsetOrange = Color(0xFFFF8A65)


val GradientStart = Color(0xFF8BC34A)
val GradientMiddle = Color(0xFFFFEB3B)
val GradientEnd = Color(0xFFA1887F)


val SurfaceLight = Color(0xFFFAF8F5)
val SurfaceDark = Color(0xFF1C1B1F)
val OnSurfaceLight = Color(0xFF1C1B1F)
val OnSurfaceDark = Color(0xFFE6E1E5)


val SuccessGreen = Color(0xFF4CAF50)
val WarningAmber = Color(0xFFFF9800)
val ErrorRed = Color(0xFFF44336)
val InfoBlue = Color(0xFF2196F3)


object AppGradients {
    val primaryGradient = Brush.horizontalGradient(
        colors = listOf(GradientStart, GradientMiddle)
    )

    val earthGradient = Brush.verticalGradient(
        colors = listOf(EarthGreen80, EarthGreen40)
    )

    val warmGradient = Brush.horizontalGradient(
        colors = listOf(GoldenYellow80, SunsetOrange)
    )

    val sunsetGradient = Brush.linearGradient(
        colors = listOf(GoldenYellow70, SunsetOrange, WarmBrown60)
    )

    val cardGradient = Brush.verticalGradient(
        colors = listOf(SoftCream, LightBeige)
    )
}