package com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.preparation_mode_with_goal
import pushuptrainermobile.shared.generated.resources.preparation_mode_without_goal

/** Переключатель режима тренировки: «С целью» / «Без цели». */
@Composable
fun PreparationModeSwitch(
    selectedMode: PreparationMode,
    onModeSelect: (PreparationMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.colors.surface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PreparationModeTab(
            text = stringResource(Res.string.preparation_mode_with_goal),
            isSelected = selectedMode == PreparationMode.WithGoal,
            onClick = { onModeSelect(PreparationMode.WithGoal) },
            modifier = Modifier.weight(1f),
        )
        PreparationModeTab(
            text = stringResource(Res.string.preparation_mode_without_goal),
            isSelected = selectedMode == PreparationMode.WithoutGoal,
            onClick = { onModeSelect(PreparationMode.WithoutGoal) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PreparationModeTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (isSelected) AppTheme.colors.accent else Color.Transparent
    val textColor = if (isSelected) AppTheme.colors.onAccent else AppTheme.colors.textSecondary
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = AppTheme.typography.button,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun PreparationModeSwitchWithGoalPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            PreparationModeSwitch(
                selectedMode = PreparationMode.WithGoal,
                onModeSelect = {},
                modifier = Modifier.padding(20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun PreparationModeSwitchWithoutGoalPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            PreparationModeSwitch(
                selectedMode = PreparationMode.WithoutGoal,
                onModeSelect = {},
                modifier = Modifier.padding(20.dp),
            )
        }
    }
}
