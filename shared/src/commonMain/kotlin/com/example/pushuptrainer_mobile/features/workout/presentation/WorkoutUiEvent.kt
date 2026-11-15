package com.example.pushuptrainer_mobile.features.workout.presentation

sealed interface WorkoutUiEvent {
    data object FinishClicked : WorkoutUiEvent
    data object BackClicked : WorkoutUiEvent
    data object ConfirmFinishClicked : WorkoutUiEvent
    data object DismissFinishClicked : WorkoutUiEvent
    data object ResumeClicked : WorkoutUiEvent
    data object RetrySaveClicked : WorkoutUiEvent
    data object ShowResultClicked : WorkoutUiEvent
}
