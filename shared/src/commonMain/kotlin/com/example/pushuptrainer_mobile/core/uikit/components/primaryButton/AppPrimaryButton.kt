package com.example.pushuptrainer_mobile.core.uikit.components.primaryButton

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme

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
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        color = AppTheme.colors.accent,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = AppTheme.typography.button,
                color = AppTheme.colors.onAccent,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
private fun AppPrimaryButtonLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        AppPrimaryButtonPreviewContent()
    }
}

@Preview
@Composable
private fun AppPrimaryButtonDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        AppPrimaryButtonPreviewContent()
    }
}

@Composable
private fun AppPrimaryButtonPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        Column(modifier = Modifier.padding(20.dp)) {
            AppPrimaryButton(text = "Начать новую тренировку", onClick = {})
        }
    }
}
