package com.example.pushuptrainer_mobile.core.uikit.components.emptyProgressCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.components.primaryButton.AppPrimaryButton
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme

@Composable
fun EmptyProgressCard(model: EmptyProgressUiModel, onPrepareClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface).padding(horizontal = 16.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(model.title, style = AppTheme.typography.cardTitle, color = AppTheme.colors.textPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Text(model.description, style = AppTheme.typography.body, color = AppTheme.colors.textSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(42.dp))
        AppPrimaryButton(text = model.actionLabel, onClick = onPrepareClick)
    }
}

@Preview(widthDp = 320)
@Composable
private fun EmptyProgressLightPreview() {
    PushUpTrainerTheme(darkTheme = false) { EmptyProgressPreview() }
}

@Preview(widthDp = 320)
@Composable
private fun EmptyProgressDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) { EmptyProgressPreview() }
}

@Composable
private fun EmptyProgressPreview() {
    EmptyProgressCard(
        EmptyProgressUiModel("Здесь появится ваш прогресс", "Проведите первую тренировку,\nчтобы увидеть статистику и рекорды.", "Подготовиться"),
        onPrepareClick = {},
    )
}
