package com.example.pushuptrainer_mobile.features.onboarding.presentation

/** Действия пользователя в онбординге. */
sealed interface OnboardingUiEvent {

    /** Нажата кнопка «Далее». */
    data object NextClicked : OnboardingUiEvent

    /** Нажата кнопка «Назад». */
    data object BackClicked : OnboardingUiEvent

    /** Нажата кнопка «Понятно» на последнем шаге. */
    data object FinishClicked : OnboardingUiEvent

    /**
     * Пользователь пролистал страницы пальцем и остановился на шаге [step].
     * Кнопки «Далее»/«Назад» этим событием не пользуются: у них свои события.
     */
    data class StepSwiped(val step: OnboardingStep) : OnboardingUiEvent

    /** Нажат крестик в шапке. */
    data object CloseClicked : OnboardingUiEvent
}
