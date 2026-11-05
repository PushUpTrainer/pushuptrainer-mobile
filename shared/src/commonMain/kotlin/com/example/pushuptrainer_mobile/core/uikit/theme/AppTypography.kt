package com.example.pushuptrainer_mobile.core.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class AppTypography(
    val screenTitle: TextStyle,
    val display: TextStyle,
    val cardTitleLarge: TextStyle,
    val cardTitle: TextStyle,
    val button: TextStyle,
    val labelMedium: TextStyle,
    val body: TextStyle,
    val caption: TextStyle,
    val navLabel: TextStyle,
)

private val BaseFontFamily = FontFamily.Default

val DefaultAppTypography = AppTypography(
    screenTitle = TextStyle(fontFamily = BaseFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 34.sp),
    display = TextStyle(fontFamily = BaseFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 48.sp, lineHeight = 56.sp),
    cardTitleLarge = TextStyle(fontFamily = BaseFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp),
    cardTitle = TextStyle(fontFamily = BaseFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    button = TextStyle(fontFamily = BaseFontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = BaseFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    body = TextStyle(fontFamily = BaseFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    caption = TextStyle(fontFamily = BaseFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp),
    navLabel = TextStyle(fontFamily = BaseFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp),
)

val LocalAppTypography = staticCompositionLocalOf { DefaultAppTypography }
