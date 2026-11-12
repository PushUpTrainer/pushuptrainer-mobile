package com.example.pushuptrainer_mobile.features.home.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekDayUiModel
import com.example.pushuptrainer_mobile.features.home.domain.model.HomeHistory
import kotlinx.datetime.LocalDate

internal fun HomeHistory.toUiState() = HomeUiState.Content(
    week = WeekCalendarUiModel(
        rangeText = "${week.first().dayAndMonth()} - ${week.last().dayAndMonth()}",
        days = week.map { day ->
            WeekDayUiModel(
                dayOfMonth = day.day,
                isToday = day == today,
                hasWorkout = day in workoutDays,
                epochDay = day.toEpochDays(),
                isSelected = day == selectedDate,
                enabled = day <= today,
                dateLabel = day.toString(),
            )
        },
        canGoForward = week.last() < today,
    ),
    today = selectedDate.takeUnless { it == today }?.let { "${it.dayAndMonth()}.${it.year}" }.let { dateLabel ->
        if (workouts == 0) TodaySummaryUiModel.Empty(dateLabel)
        else TodaySummaryUiModel.Done(repetitions, workouts, dateLabel)
    },
    streak = StreakCardUiModel(daysInRow = streakDays),
    todayEpochDay = today.toEpochDays(),
    selectedEpochDay = selectedDate.toEpochDays(),
    hasWorkoutToday = hasWorkoutToday,
)

private fun LocalDate.dayAndMonth() =
    "${day.toString().padStart(2, '0')}.${(month.ordinal + 1).toString().padStart(2, '0')}"
