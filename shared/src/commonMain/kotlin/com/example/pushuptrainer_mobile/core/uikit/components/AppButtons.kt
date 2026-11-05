package com.example.pushuptrainer_mobile.core.uikit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme

private val ButtonShape = RoundedCornerShape(14.dp)
private val ButtonHeight = 52.dp

/** Основная кнопка: залитая акцентным цветом. */
@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonHeight),
        shape = ButtonShape,
        color = AppTheme.colors.accent,
    ) {
        ButtonLabel(
            text = text,
            color = AppTheme.colors.onAccent,
        )
    }
}

/** Второстепенная кнопка: спокойный фон с обводкой. */
@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonHeight),
        shape = ButtonShape,
        color = AppTheme.colors.secondaryButton,
        border = BorderStroke(1.dp, AppTheme.colors.outline),
    ) {
        ButtonLabel(
            text = text,
            color = AppTheme.colors.textPrimary.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun ButtonLabel(
    text: String,
    color: Color,
) {
    Box(
        modifier = Modifier.padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = AppTheme.typography.button,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun AppButtonsLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        AppButtonsPreviewContent()
    }
}

@Preview
@Composable
private fun AppButtonsDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        AppButtonsPreviewContent()
    }
}

@Composable
private fun AppButtonsPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AppPrimaryButton(text = "Начать новую тренировку", onClick = {})
            AppSecondaryButton(text = "На главный экран", onClick = {})
        }
    }
}
