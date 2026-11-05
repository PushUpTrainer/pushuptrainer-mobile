package com.example.pushuptrainer_mobile.core.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val accentText: Color,
    val accentContainer: Color,
    val onAccent: Color,
    val secondaryButton: Color,
    val outline: Color,
)

val LightAppColors = AppColors(
    background = Color(0xFFE5DED4),
    surface = Color(0xFFEEE7DD),
    textPrimary = Color(0xFF322B26),
    textSecondary = Color(0xFF5F554D),
    accent = Color(0xFFA34D2C),
    accentText = Color(0xFF77462E),
    accentContainer = Color(0xFFE2CFBC),
    onAccent = Color(0xFFFFF4E8),
    secondaryButton = Color(0xFFDDD3C6),
    outline = Color(0xFFC7BAAC),
)

val DarkAppColors = AppColors(
    background = Color(0xFF141416),
    surface = Color(0xFF202022),
    textPrimary = Color(0xFFF2F2F7),
    textSecondary = Color(0xFFB3B3BB),
    accent = Color(0xFFEE8755),
    accentText = Color(0xFFF2B18C),
    accentContainer = Color(0xFF30231E),
    onAccent = Color(0xFF20150F),
    secondaryButton = Color(0xFF29292C),
    outline = Color(0xFF38383D),
)

val LocalAppColors = staticCompositionLocalOf { LightAppColors }
