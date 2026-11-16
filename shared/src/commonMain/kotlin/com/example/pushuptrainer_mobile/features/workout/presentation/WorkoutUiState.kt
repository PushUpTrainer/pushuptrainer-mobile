package com.example.pushuptrainer_mobile.features.workout.presentation

import androidx.compose.runtime.Immutable
import com.example.pushuptrainer_mobile.core.uikit.components.workoutCounter.WorkoutCounterUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.workoutHint.WorkoutHintUiModel

@Immutable
data class WorkoutUiState(
    val repetitions: String,
    val mode: WorkoutModeUiModel,
    val hint: WorkoutHintUiModel?,
    val status: WorkoutStatus = WorkoutStatus.Running,
    val cameraGeneration: Int = 0,
    val showFinishConfirmation: Boolean = false,
) {
    val counter: WorkoutCounterUiModel
        get() = when (val currentMode = mode) {
            WorkoutModeUiModel.Free -> WorkoutCounterUiModel.Free(repetitions)
            is WorkoutModeUiModel.Goal -> WorkoutCounterUiModel.Goal(repetitions, currentMode.target)
        }
}

enum class WorkoutStatus { Starting, Running, Paused, CameraError, Saving, SaveError, Saved, Discarded }
