package com.example.pushuptrainer_mobile.features.preparation.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.goalSelectorCard.GoalSelectorUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationMode

/** Состояние экрана подготовки к тренировке (макеты 1.1.1 «Без цели», 1.1.2 «С целью»). */
data class PreparationUiState(
    val selectedMode: PreparationMode,
    val goal: GoalSelectorUiModel,
)
