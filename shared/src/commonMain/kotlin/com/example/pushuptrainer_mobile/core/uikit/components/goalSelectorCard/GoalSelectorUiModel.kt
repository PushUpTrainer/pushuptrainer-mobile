package com.example.pushuptrainer_mobile.core.uikit.components.goalSelectorCard

/**
 * Данные карточки «Цель тренировки»: текущее число повторений и доступность кнопок «−» / «+».
 * Границы вычисляются в presentation-слое, чтобы карточка только отображала готовое состояние.
 */
data class GoalSelectorUiModel(
    val goal: Int,
    val canDecrease: Boolean,
    val canIncrease: Boolean,
)
