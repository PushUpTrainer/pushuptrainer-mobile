package com.example.pushuptrainer_mobile.features.progress.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard.DaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard.EmptyDayUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthCalendarUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthDayPeriodUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthDayUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.features.progress.domain.model.ProgressHistory
import kotlin.math.roundToLong
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus

internal fun ProgressHistory.toUiState(): ProgressUiState.Content {
    val first = LocalDate(selectedDate.year, selectedDate.month, 1)
    val next = first.plus(1, DateTimeUnit.MONTH)
    val cells = List<MonthDayUiModel?>(first.dayOfWeek.isoDayNumber - 1) { null } +
        (first.toEpochDays() until next.toEpochDays()).map { epochDay ->
            val day = LocalDate.fromEpochDays(epochDay)
            MonthDayUiModel(
                dayOfMonth = day.day,
                period = when {
                    day < today -> MonthDayPeriodUiModel.Past
                    day == today -> MonthDayPeriodUiModel.Today
                    else -> MonthDayPeriodUiModel.Future
                },
                hasWorkout = day in workoutDays,
                epochDay = epochDay,
                isSelected = day == selectedDate,
                dateLabel = day.toString(),
            )
        }
    val months = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    val dateMonths = listOf("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря")
    val date = "${selectedDate.day} ${dateMonths[selectedDate.month.ordinal]}" +
        when {
            selectedDate == today -> " · Сегодня"
            selectedDate.year != today.year -> " ${selectedDate.year}"
            else -> ""
        }
    val averageTenths = (averageRepetitions * 10).roundToLong()
    val average = (averageTenths / 10).toString() +
        if (averageTenths % 10 == 0L) "" else ",${averageTenths % 10}"
    return ProgressUiState.Content(
        calendar = MonthCalendarUiModel(
            title = "${months[first.month.ordinal]} ${first.year}",
            weeks = cells.chunked(7).map { it + List(7 - it.size) { null } },
            canGoForward = next <= today,
        ),
        day = DaySummaryUiModel(
            date, dayWorkouts, dayRepetitions,
            emptyDay = when {
                dayWorkouts > 0 -> null
                selectedDate == today -> EmptyDayUiModel.Today
                else -> EmptyDayUiModel.Past
            },
        ),
        totalRepetitions = totalRepetitions.toString(),
        bestWorkout = bestWorkout.toString(),
        totalWorkouts = totalWorkouts.toString(),
        averageRepetitions = average,
        streak = StreakCardUiModel(streakDays, showStartPrompt = streakDays == 0),
        isStreakHighlighted = today in workoutDays,
        isHistoryEmpty = totalWorkouts == 0,
        selectedEpochDay = selectedDate.toEpochDays(),
        todayEpochDay = today.toEpochDays(),
    )
}
