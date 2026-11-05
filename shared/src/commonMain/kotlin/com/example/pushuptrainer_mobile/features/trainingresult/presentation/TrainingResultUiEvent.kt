package com.example.pushuptrainer_mobile.features.trainingresult.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.AppBottomBarDestination

/** Действия пользователя на экране результата тренировки. */
sealed interface TrainingResultUiEvent {

    /** Нажата кнопка «Начать новую тренировку». */
    data object StartNewTrainingClicked : TrainingResultUiEvent

    /** Нажата кнопка «На главный экран». */
    data object GoToMainClicked : TrainingResultUiEvent

    /** Нажата кнопка «Тема». */
    data object ThemeToggleClicked : TrainingResultUiEvent

    /** Выбран раздел в нижней навигации. */
    data class BottomBarDestinationClicked(
        val destination: AppBottomBarDestination,
    ) : TrainingResultUiEvent
}
