package com.example.pushuptrainer_mobile.features.workout.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.components.primaryButton.AppPrimaryButton
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.components.workoutCounter.WorkoutCounter
import com.example.pushuptrainer_mobile.core.uikit.components.workoutHint.WorkoutHint
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.workout_finish
import pushuptrainermobile.shared.generated.resources.workout_finish_early
import pushuptrainermobile.shared.generated.resources.workout_starting
import pushuptrainermobile.shared.generated.resources.workout_camera_error
import pushuptrainermobile.shared.generated.resources.workout_paused
import pushuptrainermobile.shared.generated.resources.workout_resume
import pushuptrainermobile.shared.generated.resources.workout_retry
import pushuptrainermobile.shared.generated.resources.workout_saving
import pushuptrainermobile.shared.generated.resources.workout_save_error
import pushuptrainermobile.shared.generated.resources.workout_saved
import pushuptrainermobile.shared.generated.resources.workout_show_result
import pushuptrainermobile.shared.generated.resources.workout_confirm_title
import pushuptrainermobile.shared.generated.resources.workout_confirm_message
import pushuptrainermobile.shared.generated.resources.workout_stay

@Composable
fun WorkoutScreen(
    state: WorkoutUiState,
    onEvent: (WorkoutUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    cameraContent: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Box(modifier = Modifier.matchParentSize(), content = cameraContent)
        BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WorkoutCounter(
                    model = state.counter,
                    modifier = Modifier.widthIn(max = 240.dp).fillMaxWidth(),
                )
                Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                    val statusText = when (state.status) {
                        WorkoutStatus.Starting -> Res.string.workout_starting
                        WorkoutStatus.Paused -> Res.string.workout_paused
                        WorkoutStatus.CameraError -> Res.string.workout_camera_error
                        WorkoutStatus.Saving -> Res.string.workout_saving
                        WorkoutStatus.SaveError -> Res.string.workout_save_error
                        WorkoutStatus.Saved -> Res.string.workout_saved
                        WorkoutStatus.Running -> null
                    }
                    statusText?.let {
                        Text(stringResource(it), color = AppTheme.colors.textPrimary, style = AppTheme.typography.body)
                        Spacer(Modifier.height(16.dp))
                    }
                    state.hint?.takeIf { state.status == WorkoutStatus.Running }?.let { hint ->
                        WorkoutHint(hint, Modifier.fillMaxWidth())
                        Spacer(Modifier.height(32.dp))
                    }
                    when (state.status) {
                        WorkoutStatus.Paused, WorkoutStatus.CameraError -> {
                            AppPrimaryButton(
                                text = stringResource(if (state.status == WorkoutStatus.Paused) Res.string.workout_resume else Res.string.workout_retry),
                                onClick = { onEvent(WorkoutUiEvent.ResumeClicked) },
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        WorkoutStatus.SaveError -> AppPrimaryButton(
                            text = stringResource(Res.string.workout_retry),
                            onClick = { onEvent(WorkoutUiEvent.RetrySaveClicked) },
                        )
                        WorkoutStatus.Saved -> AppPrimaryButton(
                            text = stringResource(Res.string.workout_show_result),
                            onClick = { onEvent(WorkoutUiEvent.ShowResultClicked) },
                        )
                        else -> Unit
                    }
                    if (state.status !in setOf(WorkoutStatus.Saving, WorkoutStatus.SaveError, WorkoutStatus.Saved)) AppPrimaryButton(
                        text = stringResource(
                            if (state.mode is WorkoutModeUiModel.Goal) Res.string.workout_finish_early
                            else Res.string.workout_finish,
                        ),
                        onClick = { onEvent(WorkoutUiEvent.FinishClicked) },
                    )
                }
            }
        }
        if (state.showFinishConfirmation) {
            AlertDialog(
                onDismissRequest = { onEvent(WorkoutUiEvent.DismissFinishClicked) },
                title = { Text(stringResource(Res.string.workout_confirm_title)) },
                text = { Text(stringResource(Res.string.workout_confirm_message)) },
                confirmButton = {
                    TextButton(onClick = { onEvent(WorkoutUiEvent.ConfirmFinishClicked) }) {
                        Text(stringResource(Res.string.workout_finish))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onEvent(WorkoutUiEvent.DismissFinishClicked) }) {
                        Text(stringResource(Res.string.workout_stay))
                    }
                },
            )
        }
    }
}


@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun WorkoutPausedPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        WorkoutScreen(WorkoutUiState("12", WorkoutModeUiModel.Goal("20"), null, WorkoutStatus.Paused), {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun WorkoutSaveErrorPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        WorkoutScreen(WorkoutUiState("12", WorkoutModeUiModel.Free, null, WorkoutStatus.SaveError), {})
    }
}
