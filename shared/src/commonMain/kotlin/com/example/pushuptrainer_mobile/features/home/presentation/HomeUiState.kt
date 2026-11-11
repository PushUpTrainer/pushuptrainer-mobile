package com.example.pushuptrainer_mobile.features.home.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarUiModel

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data object Unavailable : HomeUiState

    data class Content(
        val week: WeekCalendarUiModel,
        val today: TodaySummaryUiModel,
        val streak: StreakCardUiModel,
    ) : HomeUiState
}
