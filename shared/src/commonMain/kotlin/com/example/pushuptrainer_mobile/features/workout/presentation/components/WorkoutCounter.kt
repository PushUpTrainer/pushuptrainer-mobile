package com.example.pushuptrainer_mobile.features.workout.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import com.example.pushuptrainer_mobile.features.workout.presentation.WorkoutCounterUiModel
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.workout_free_mode
import pushuptrainermobile.shared.generated.resources.workout_goal_mode
import pushuptrainermobile.shared.generated.resources.workout_repetitions

@Composable
fun WorkoutCounter(model: WorkoutCounterUiModel, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = AppTheme.colors.background.copy(alpha = if (AppTheme.isDarkTheme) 0.75f else 0.8f),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().heightIn(min = 28.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.workout_repetitions),
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textSecondary,
                )
                Text(
                    text = stringResource(
                        if (model is WorkoutCounterUiModel.Goal) Res.string.workout_goal_mode
                        else Res.string.workout_free_mode,
                    ),
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.accentText,
                )
            }
            val secondaryColor = AppTheme.colors.textSecondary
            Text(
                text = buildAnnotatedString {
                    append(model.repetitions)
                    if (model is WorkoutCounterUiModel.Goal) {
                        withStyle(SpanStyle(fontSize = 24.sp, color = secondaryColor)) {
                            append("/")
                            append(model.target)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(with(LocalDensity.current) { 64.sp.toDp() }),
                style = AppTheme.typography.display.copy(
                    fontSize = 60.sp,
                    lineHeight = 64.sp,
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None,
                        mode = LineHeightStyle.Mode.Tight,
                    ),
                ),
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
private fun WorkoutCounterFreePreview() {
    PushUpTrainerTheme(darkTheme = true) {
        WorkoutCounter(WorkoutCounterUiModel.Free("16"), Modifier.fillMaxWidth())
    }
}

@Preview
@Composable
private fun WorkoutCounterGoalPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        WorkoutCounter(WorkoutCounterUiModel.Goal("16", "20"), Modifier.fillMaxWidth())
    }
}
