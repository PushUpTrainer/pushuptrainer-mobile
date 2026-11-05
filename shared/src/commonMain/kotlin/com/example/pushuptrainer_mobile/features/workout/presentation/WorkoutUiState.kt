package com.example.pushuptrainer_mobile.features.workout.presentation

import androidx.compose.runtime.Immutable

@Immutable
data class WorkoutUiState(
    val counter: WorkoutCounterUiModel,
    val hint: WorkoutHintUiModel,
)
