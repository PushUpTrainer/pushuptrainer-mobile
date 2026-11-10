package com.example.pushuptrainer_mobile.features.preparation.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.goalSelectorCard.GoalSelectorUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationMode

/** Тестовые данные для Preview экрана подготовки (значения из макета Figma). */
internal object PreparationPreviewData {

    val withGoal = PreparationUiState(
        selectedMode = PreparationMode.WithGoal,
        goal = GoalSelectorUiModel(goal = 20, canDecrease = true, canIncrease = true),
    )

    val withoutGoal = PreparationUiState(
        selectedMode = PreparationMode.WithoutGoal,
        goal = GoalSelectorUiModel(goal = 20, canDecrease = true, canIncrease = true),
    )

    val goalAtMinimum = withGoal.copy(
        goal = GoalSelectorUiModel(goal = 1, canDecrease = false, canIncrease = true),
    )

    val hintVisible = withGoal.copy(isHowToPlacePhoneVisible = true)
}
