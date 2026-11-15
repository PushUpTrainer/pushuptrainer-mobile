package com.example.pushuptrainer_mobile.features.onboarding.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.stepIndicator.StepIndicatorUiModel

/** Состояние онбординга: какой шаг сейчас открыт. */
data class OnboardingUiState(
    val step: OnboardingStep,
) {
    /** На первом шаге нет кнопки «Назад». */
    val isFirstStep: Boolean
        get() = step.ordinal == 0

    /** На последнем шаге вместо «Далее» кнопка «Понятно». */
    val isLastStep: Boolean
        get() = step.ordinal == OnboardingStep.entries.lastIndex

    val stepIndicator: StepIndicatorUiModel
        get() = StepIndicatorUiModel(
            stepCount = OnboardingStep.entries.size,
            currentStepIndex = step.ordinal,
        )
}
