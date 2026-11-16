package com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard

/** Итог одного дня: подпись даты, число тренировок и повторений. */
data class DaySummaryUiModel(
    val dateText: String,
    val trainings: Int,
    val repetitions: Int,
    val emptyDay: EmptyDayUiModel? = null,
)

enum class EmptyDayUiModel { Today, Past }
