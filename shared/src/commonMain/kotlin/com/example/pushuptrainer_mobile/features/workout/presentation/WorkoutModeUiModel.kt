package com.example.pushuptrainer_mobile.features.workout.presentation

import androidx.compose.runtime.Immutable

@Immutable
sealed interface WorkoutModeUiModel {
    data object Free : WorkoutModeUiModel

    data class Goal(val target: String) : WorkoutModeUiModel
}
