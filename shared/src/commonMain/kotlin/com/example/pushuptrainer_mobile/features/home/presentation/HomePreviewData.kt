package com.example.pushuptrainer_mobile.features.home.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekDayUiModel

/** Тестовые данные для Preview главного экрана (значения из макета Figma). */
internal object HomePreviewData {

    private fun week(hasWorkoutToday: Boolean) = WeekCalendarUiModel(
        rangeText = "19–25 октября",
        days = (19..25).map { day ->
            val isToday = day == 19
            WeekDayUiModel(
                dayOfMonth = day,
                isToday = isToday,
                hasWorkout = isToday && hasWorkoutToday,
            )
        },
    )

    /** Макет 1.0.0: сегодня тренировок ещё не было. */
    val beforeWorkout = HomeUiState.Content(
        streak = StreakCardUiModel(daysInRow = 2),
        week = week(hasWorkoutToday = false),
        today = TodaySummaryUiModel.Empty,
    )

    /** Макет 1.0.1: сегодня тренировка уже была. */
    val afterWorkout = HomeUiState.Content(
        streak = StreakCardUiModel(daysInRow = 3),
        week = week(hasWorkoutToday = true),
        today = TodaySummaryUiModel.Done(repetitions = 16, trainings = 1),
    )
}
