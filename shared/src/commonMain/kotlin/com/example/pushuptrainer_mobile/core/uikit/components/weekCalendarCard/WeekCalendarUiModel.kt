package com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard

import androidx.compose.runtime.Immutable

@Immutable
data class WeekCalendarUiModel(
    val rangeText: String,
    val days: List<WeekDayUiModel>,
    val canGoForward: Boolean = false,
)

@Immutable
data class WeekDayUiModel(
    val dayOfMonth: Int,
    val isToday: Boolean,
    val hasWorkout: Boolean,
    val epochDay: Long = 0,
    val isSelected: Boolean = isToday,
    val enabled: Boolean = true,
    val dateLabel: String = dayOfMonth.toString(),
)
