package com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard

/** Что показывает карточка «Сегодня». */
sealed interface TodaySummaryUiModel {

    /** Сегодня тренировок ещё не было (макет 1.0.0). */
    data object Empty : TodaySummaryUiModel

    /** Сегодня уже были тренировки (макет 1.0.1). */
    data class Done(
        val repetitions: Int,
        val trainings: Int,
    ) : TodaySummaryUiModel
}
