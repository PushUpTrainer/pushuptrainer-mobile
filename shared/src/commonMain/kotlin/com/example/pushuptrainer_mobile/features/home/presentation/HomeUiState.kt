package com.example.pushuptrainer_mobile.features.home.presentation

import androidx.compose.runtime.Immutable
import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarUiModel

@Immutable
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Unavailable : HomeUiState

    data class Content(
        val week: WeekCalendarUiModel,
        val today: TodaySummaryUiModel,
        val streak: StreakCardUiModel,
        val todayEpochDay: Long = 0,
        val selectedEpochDay: Long = todayEpochDay,
        val hasWorkoutToday: Boolean = today is TodaySummaryUiModel.Done,
        val historyUnavailable: Boolean = false,
    ) : HomeUiState
}
