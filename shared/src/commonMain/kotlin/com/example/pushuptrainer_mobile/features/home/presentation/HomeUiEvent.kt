package com.example.pushuptrainer_mobile.features.home.presentation

sealed interface HomeUiEvent {
    data class DaySelected(val epochDay: Long) : HomeUiEvent
    data object PreviousWeekClicked : HomeUiEvent
    data object NextWeekClicked : HomeUiEvent
    data object TodayClicked : HomeUiEvent
    data object ThemeToggleClicked : HomeUiEvent
    data object OnboardingClicked : HomeUiEvent
    data object PrepareClicked : HomeUiEvent
    data object RetryClicked : HomeUiEvent
}
