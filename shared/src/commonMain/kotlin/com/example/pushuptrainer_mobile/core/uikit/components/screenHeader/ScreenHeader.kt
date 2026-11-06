package com.example.pushuptrainer_mobile.core.uikit.components.screenHeader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.components.themeToggleButton.ThemeToggleButton
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme

/** Шапка экрана: заголовок, подзаголовок и кнопка «Тема». */
@Composable
fun ScreenHeader(
    model: ScreenHeaderUiModel,
    onThemeToggleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = model.title,
                style = AppTheme.typography.screenTitle,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            ThemeToggleButton(
                isDarkTheme = AppTheme.isDarkTheme,
                onClick = onThemeToggleClick,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = model.subtitle,
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
        )
    }
}

@Preview
@Composable
private fun ScreenHeaderLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        ScreenHeaderPreviewContent()
    }
}

@Preview
@Composable
private fun ScreenHeaderDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        ScreenHeaderPreviewContent()
    }
}

@Composable
private fun ScreenHeaderPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        ScreenHeader(
            model = ScreenHeaderUiModel(
                title = "Тренировка",
                subtitle = "Повторения и техника — под контролем",
            ),
            onThemeToggleClick = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}
