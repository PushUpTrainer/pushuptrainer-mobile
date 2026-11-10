package com.example.pushuptrainer_mobile.features.home.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarUiModel

sealed interface HomeUiState {
    val canPrepare: Boolean

    data class Loading(override val canPrepare: Boolean) : HomeUiState

    data class Unavailable(override val canPrepare: Boolean) : HomeUiState

    data class Content(
        val week: WeekCalendarUiModel,
        val today: TodaySummaryUiModel,
        val streak: StreakCardUiModel? = null,
        override val canPrepare: Boolean = true,
    ) : HomeUiState
}
