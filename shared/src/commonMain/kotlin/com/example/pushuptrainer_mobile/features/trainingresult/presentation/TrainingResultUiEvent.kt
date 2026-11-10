package com.example.pushuptrainer_mobile.features.trainingresult.presentation

sealed interface TrainingResultUiEvent {
    data object StartNewTrainingClicked : TrainingResultUiEvent
    data object GoToMainClicked : TrainingResultUiEvent
}
