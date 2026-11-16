package com.example.pushuptrainer_mobile.features.progress.presentation

internal sealed interface ProgressUiEvent {
    data class DaySelected(val epochDay: Long) : ProgressUiEvent
    data object PreviousMonthClicked : ProgressUiEvent
    data object NextMonthClicked : ProgressUiEvent
    data object TodayClicked : ProgressUiEvent
    data object ThemeToggleClicked : ProgressUiEvent
    data object RetryClicked : ProgressUiEvent
}

internal enum class ProgressUiEffect { ToggleTheme }
