package com.example.pushuptrainer_mobile.features.workout.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import com.example.pushuptrainer_mobile.features.workout.presentation.WorkoutHintUiModel

@Composable
fun WorkoutHint(model: WorkoutHintUiModel, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = AppTheme.colors.background.copy(alpha = if (AppTheme.isDarkTheme) 0.75f else 0.8f),
    ) {
        Column(
            modifier = Modifier.heightIn(min = 72.dp).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(model.title, style = AppTheme.typography.cardTitle, color = AppTheme.colors.textPrimary)
            Text(model.message, style = AppTheme.typography.labelMedium, color = AppTheme.colors.textPrimary)
        }
    }
}

@Preview
@Composable
private fun WorkoutHintReadyPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        WorkoutHint(WorkoutHintUiModel("Тело в кадре", "Подсказки по технике"), Modifier.fillMaxWidth())
    }
}

@Preview
@Composable
private fun WorkoutHintNoPersonPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        WorkoutHint(WorkoutHintUiModel("Тело не видно", "Встаньте так, чтобы тело было в кадре"), Modifier.fillMaxWidth())
    }
}
