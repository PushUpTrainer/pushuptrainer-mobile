package com.example.pushuptrainer_mobile.features.trainingresult.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.text.russianPluralString
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import com.example.pushuptrainer_mobile.features.trainingresult.presentation.TrainingOutcomeUiModel
import com.example.pushuptrainer_mobile.features.trainingresult.presentation.TrainingResultPreviewData
import com.example.pushuptrainer_mobile.features.trainingresult.presentation.TrainingResultSummaryUiModel
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.training_result_of_goal_repetitions_many
import pushuptrainermobile.shared.generated.resources.training_result_of_goal_repetitions_one
import pushuptrainermobile.shared.generated.resources.training_result_repetitions_few
import pushuptrainermobile.shared.generated.resources.training_result_repetitions_many
import pushuptrainermobile.shared.generated.resources.training_result_repetitions_one
import pushuptrainermobile.shared.generated.resources.training_result_status_goal_reached
import pushuptrainermobile.shared.generated.resources.training_result_status_no_goal
import pushuptrainermobile.shared.generated.resources.training_result_status_stopped_early

/**
 * Карточка с итогом тренировки: иконка, число повторений, подпись и статус.
 *
 * @param iconAppearance прогресс появления иконки от 0 до 1 (анимация появления результата).
 */
@Composable
fun TrainingResultSummaryCard(
    model: TrainingResultSummaryUiModel,
    modifier: Modifier = Modifier,
    iconAppearance: Float = 1f,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(248.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface)
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutcomeIcon(
            outcome = model.outcome,
            modifier = Modifier.graphicsLayer {
                alpha = iconAppearance
                val scale = 0.6f + 0.4f * iconAppearance
                scaleX = scale
                scaleY = scale
            },
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = model.repetitions.toString(),
            style = AppTheme.typography.display,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = repetitionsCaption(model),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(model.outcome.statusText()),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.accentText,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun OutcomeIcon(
    outcome: TrainingOutcomeUiModel,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(AppTheme.colors.accent),
        contentAlignment = Alignment.Center,
    ) {
        when (outcome) {
            TrainingOutcomeUiModel.NoGoal,
            is TrainingOutcomeUiModel.GoalReached,
            -> Text(
                text = "✓",
                style = AppTheme.typography.iconGlyph,
                color = AppTheme.colors.accentContainer,
            )

            is TrainingOutcomeUiModel.StoppedEarly -> Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AppTheme.colors.accentContainer),
            )
        }
    }
}

@Composable
private fun repetitionsCaption(model: TrainingResultSummaryUiModel): String =
    when (val outcome = model.outcome) {
        TrainingOutcomeUiModel.NoGoal -> russianPluralString(
            count = model.repetitions,
            one = Res.string.training_result_repetitions_one,
            few = Res.string.training_result_repetitions_few,
            many = Res.string.training_result_repetitions_many,
        )

        is TrainingOutcomeUiModel.GoalReached -> ofGoalRepetitions(outcome.goal)

        is TrainingOutcomeUiModel.StoppedEarly -> ofGoalRepetitions(outcome.goal)
    }

/** «из 1 повторения», «из 3 повторений», «из 20 повторений». */
@Composable
private fun ofGoalRepetitions(goal: Int): String = russianPluralString(
    count = goal,
    one = Res.string.training_result_of_goal_repetitions_one,
    few = Res.string.training_result_of_goal_repetitions_many,
    many = Res.string.training_result_of_goal_repetitions_many,
)

private fun TrainingOutcomeUiModel.statusText() = when (this) {
    TrainingOutcomeUiModel.NoGoal -> Res.string.training_result_status_no_goal
    is TrainingOutcomeUiModel.GoalReached -> Res.string.training_result_status_goal_reached
    is TrainingOutcomeUiModel.StoppedEarly -> Res.string.training_result_status_stopped_early
}

@Preview
@Composable
private fun TrainingResultSummaryCardLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        SummaryCardPreviewContent()
    }
}

@Preview
@Composable
private fun TrainingResultSummaryCardDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        SummaryCardPreviewContent()
    }
}

@Composable
private fun SummaryCardPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TrainingResultSummaryCard(model = TrainingResultPreviewData.noGoal.summary)
            TrainingResultSummaryCard(model = TrainingResultPreviewData.goalReached.summary)
            TrainingResultSummaryCard(model = TrainingResultPreviewData.stoppedEarly.summary)
            TrainingResultSummaryCard(
                model = TrainingResultPreviewData.goalReached.summary,
                iconAppearance = 0f,
            )
        }
    }
}
