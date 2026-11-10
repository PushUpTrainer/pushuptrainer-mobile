package com.example.pushuptrainer_mobile.features.main.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarUiModel

/** Состояние главного экрана. */
sealed interface MainUiState {

    /** Данные экрана ещё подготавливаются. */
    data object Loading : MainUiState

    /** Экран готов к отображению. */
    data class Content(
        val streak: StreakCardUiModel,
        val week: WeekCalendarUiModel,
        val today: TodaySummaryUiModel,
    ) : MainUiState {

        /** В макете серия подсвечена, только если сегодня уже была тренировка. */
        val isStreakHighlighted: Boolean
            get() = today is TodaySummaryUiModel.Done
    }
}
