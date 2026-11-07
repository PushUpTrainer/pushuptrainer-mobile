package com.example.pushuptrainer_mobile.features.workout.presentation

sealed interface WorkoutUiEvent {
    data object FinishClicked : WorkoutUiEvent
}
