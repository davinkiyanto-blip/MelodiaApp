package com.xcode.melodia.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Melodia Premium Colors
val MelodiaBlack = Color(0xFF0F0F13) // Deep rich dark
val MelodiaDarkGrey = Color(0xFF1C1C23) // Slightly lighter surface
val MelodiaSurface = Color(0xFF252530) // Card surface
val MelodiaPrimary = Color(0xFF7F5AF0) // Vibrant Purple
val MelodiaSecondary = Color(0xFF2CB67D) // Success/Secondary Teal
val MelodiaAccent = Color(0xFFFF8906) // Orange Accent
val MelodiaTextPrimary = Color(0xFFFFFFFE)
val MelodiaTextSecondary = Color(0xFF94A1B2)
val MelodiaError = Color(0xFFEF4565)
val MelodiaOutline = Color(0xFF2B2C37)

// Gradients
val MelodiaGradientPrimary = Brush.horizontalGradient(
    colors = listOf(Color(0xFF7F5AF0), Color(0xFF5A3FC0))
)

val MelodiaBackgroundGradient = Brush.verticalGradient(
    colors = listOf(MelodiaBlack, Color(0xFF16161E))
)