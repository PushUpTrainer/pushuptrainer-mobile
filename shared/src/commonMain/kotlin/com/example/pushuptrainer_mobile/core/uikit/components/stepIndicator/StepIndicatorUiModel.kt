package com.example.pushuptrainer_mobile.core.uikit.components.stepIndicator

/**
 * Данные индикатора шагов.
 *
 * @param stepCount сколько всего шагов.
 * @param currentStepIndex номер текущего шага, начиная с 0.
 */
data class StepIndicatorUiModel(
    val stepCount: Int,
    val currentStepIndex: Int,
)
