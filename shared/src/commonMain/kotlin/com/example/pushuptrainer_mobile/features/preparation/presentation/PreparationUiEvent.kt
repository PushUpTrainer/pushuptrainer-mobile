package com.example.pushuptrainer_mobile.features.preparation.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationMode

/** Действия пользователя на экране подготовки. */
sealed interface PreparationUiEvent {

    /** Выбран режим тренировки в переключателе. */
    data class ModeSelected(val mode: PreparationMode) : PreparationUiEvent

    /** Нажата кнопка «−» у счётчика цели. */
    data object GoalDecreaseClicked : PreparationUiEvent

    /** Нажата кнопка «+» у счётчика цели. */
    data object GoalIncreaseClicked : PreparationUiEvent

    /** Нажата строка «Как поставить телефон». */
    data object HowToPlacePhoneClicked : PreparationUiEvent

    /** Нажата кнопка «Начать тренировку». */
    data object StartTrainingClicked : PreparationUiEvent

    /** Нажата кнопка «Тема». */
    data object ThemeToggleClicked : PreparationUiEvent

    /** Выбран раздел в нижней навигации. */
    data class BottomBarDestinationClicked(
        val destination: BottomNavigationDestination,
    ) : PreparationUiEvent
}
