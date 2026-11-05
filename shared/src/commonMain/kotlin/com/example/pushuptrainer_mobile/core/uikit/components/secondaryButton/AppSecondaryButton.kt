package com.example.pushuptrainer_mobile.core.uikit.components.secondaryButton

import androidx.compose.foundation.BorderStroke
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
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        color = AppTheme.colors.secondaryButton,
        border = BorderStroke(1.dp, AppTheme.colors.outline),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = AppTheme.typography.button,
                color = AppTheme.colors.textPrimary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
private fun AppSecondaryButtonLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        AppSecondaryButtonPreviewContent()
    }
}

@Preview
@Composable
private fun AppSecondaryButtonDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        AppSecondaryButtonPreviewContent()
    }
}

@Composable
private fun AppSecondaryButtonPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        Column(modifier = Modifier.padding(20.dp)) {
            AppSecondaryButton(text = "На главный экран", onClick = {})
        }
    }
}
