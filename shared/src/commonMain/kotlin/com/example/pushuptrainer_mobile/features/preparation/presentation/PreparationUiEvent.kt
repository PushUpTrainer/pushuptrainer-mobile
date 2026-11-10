package com.example.pushuptrainer_mobile.features.preparation.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationMode

/** Действия пользователя на экране подготовки. */
sealed interface PreparationUiEvent {

    /** Выбран режим тренировки в переключателе. */
    data class ModeSelected(val mode: PreparationMode) : PreparationUiEvent

    /** Нажата кнопка «−» у счётчика цели. */
    data object GoalDecreaseClicked : PreparationUiEvent

    /** Нажата кнопка «+» у счётчика цели. */
    data object GoalIncreaseClicked : PreparationUiEvent

    /** Нажата строка «Как поставить телефон» — открыть подсказку. */
    data object HowToPlacePhoneClicked : PreparationUiEvent

    /** Нажата кнопка «Понятно» в подсказке — закрыть её. */
    data object HowToPlacePhoneDismissed : PreparationUiEvent
}
