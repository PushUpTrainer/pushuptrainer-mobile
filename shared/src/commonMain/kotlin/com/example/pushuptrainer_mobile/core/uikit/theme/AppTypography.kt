package com.example.pushuptrainer_mobile.core.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Текстовые стили по макету Figma (шрифт в макете — Roboto).
 */
@Immutable
data class AppTypography(
    /** Заголовок экрана, SemiBold 28/34. */
    val screenTitle: TextStyle,
    /** Крупное число (например, количество повторений), SemiBold 48/56. */
    val display: TextStyle,
    /** Заголовок карточки, SemiBold 17/22. */
    val cardTitleLarge: TextStyle,
    /** Заголовок карточки, SemiBold 16/22. */
    val cardTitle: TextStyle,
    /** Текст кнопки, Medium 16/20. */
    val button: TextStyle,
    /** Акцентная подпись, Medium 14/20. */
    val labelMedium: TextStyle,
    /** Обычный текст, Regular 14/20. */
    val body: TextStyle,
    /** Мелкая подпись, Regular 12/17. */
    val caption: TextStyle,
    /** Подпись в нижней навигации, Medium 11/16. */
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
