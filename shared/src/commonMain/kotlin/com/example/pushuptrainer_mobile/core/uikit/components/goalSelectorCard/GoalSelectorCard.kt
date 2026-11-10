package com.example.pushuptrainer_mobile.core.uikit.components.goalSelectorCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.text.russianPluralString
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.preparation_goal_card_title
import pushuptrainermobile.shared.generated.resources.preparation_repetitions_few
import pushuptrainermobile.shared.generated.resources.preparation_repetitions_many
import pushuptrainermobile.shared.generated.resources.preparation_repetitions_one

/** Карточка «Цель тренировки»: счётчик повторений с кнопками «−» / «+». */
@Composable
fun GoalSelectorCard(
    model: GoalSelectorUiModel,
    onDecreaseClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface)
            .padding(horizontal = 16.dp)
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.preparation_goal_card_title),
            style = AppTheme.typography.cardTitle,
            color = AppTheme.colors.textPrimary,
        )
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            GoalStepperButton(
                symbol = "−",
                enabled = model.canDecrease,
                onClick = onDecreaseClick,
            )
            Text(
                text = model.goal.toString(),
                style = AppTheme.typography.display,
                color = AppTheme.colors.textPrimary,
            )
            GoalStepperButton(
                symbol = "+",
                enabled = model.canIncrease,
                onClick = onIncreaseClick,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = russianPluralString(
                count = model.goal,
                one = Res.string.preparation_repetitions_one,
                few = Res.string.preparation_repetitions_few,
                many = Res.string.preparation_repetitions_many,
            ),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun GoalStepperButton(
    symbol: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentAlpha = if (enabled) 1f else 0.35f
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(52.dp),
        shape = CircleShape,
        color = AppTheme.colors.accentContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                style = AppTheme.typography.screenTitle,
                color = AppTheme.colors.accentText.copy(alpha = contentAlpha),
            )
        }
    }
}

@Preview
@Composable
private fun GoalSelectorCardLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        GoalSelectorCardPreviewContent()
    }
}

@Preview
@Composable
private fun GoalSelectorCardDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        GoalSelectorCardPreviewContent()
    }
}

@Composable
private fun GoalSelectorCardPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GoalSelectorCard(
                model = GoalSelectorUiModel(goal = 20, canDecrease = true, canIncrease = true),
                onDecreaseClick = {},
                onIncreaseClick = {},
            )
            GoalSelectorCard(
                model = GoalSelectorUiModel(goal = 1, canDecrease = false, canIncrease = true),
                onDecreaseClick = {},
                onIncreaseClick = {},
            )
        }
    }
}
