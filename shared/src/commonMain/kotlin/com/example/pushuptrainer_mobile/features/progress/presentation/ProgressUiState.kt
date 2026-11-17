package com.example.pushuptrainer_mobile.features.progress.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard.DaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthCalendarUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel

internal sealed interface ProgressUiState {
    data object Loading : ProgressUiState
    data object Unavailable : ProgressUiState
    data class Content(
        val calendar: MonthCalendarUiModel,
        val day: DaySummaryUiModel,
        val totalRepetitions: String,
        val bestWorkout: String,
        val totalWorkouts: String,
        val averageRepetitions: String,
        val streak: StreakCardUiModel,
        val isStreakHighlighted: Boolean,
        val isHistoryEmpty: Boolean,
        val selectedEpochDay: Long,
        val todayEpochDay: Long,
    ) : ProgressUiState
}
