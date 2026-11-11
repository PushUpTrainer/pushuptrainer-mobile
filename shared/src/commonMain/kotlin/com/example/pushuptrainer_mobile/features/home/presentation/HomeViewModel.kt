package com.example.pushuptrainer_mobile.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekDayUiModel
import com.example.pushuptrainer_mobile.features.home.domain.model.HomeHistory
import com.example.pushuptrainer_mobile.features.home.domain.usecase.ObserveHomeHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

internal class HomeViewModel(
    observeHomeHistory: ObserveHomeHistoryUseCase,
) : ViewModel() {
    val state = observeHomeHistory()
        .map<HomeHistory, HomeUiState> { history ->
            HomeUiState.Content(
                week = WeekCalendarUiModel(
                    rangeText = "${history.week.first().dayAndMonth()} - ${history.week.last().dayAndMonth()}",
                    days = history.week.map { day ->
                        WeekDayUiModel(
                            dayOfMonth = day.day,
                            isToday = day == history.today,
                            hasWorkout = day in history.workoutDays,
                        )
                    },
                ),
                today = if (history.workoutsToday == 0) {
                    TodaySummaryUiModel.Empty
                } else {
                    TodaySummaryUiModel.Done(history.repetitionsToday, history.workoutsToday)
                },
                streak = StreakCardUiModel(daysInRow = history.streakDays),
            )
        }
        .catch { emit(HomeUiState.Unavailable) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), HomeUiState.Loading)
}

private fun LocalDate.dayAndMonth() =
    "${day.toString().padStart(2, '0')}.${(month.ordinal + 1).toString().padStart(2, '0')}"
