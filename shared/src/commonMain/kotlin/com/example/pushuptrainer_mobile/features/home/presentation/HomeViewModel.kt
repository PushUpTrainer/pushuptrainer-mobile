package com.example.pushuptrainer_mobile.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekDayUiModel
import com.example.pushuptrainer_mobile.features.home.domain.HomeHistory
import com.example.pushuptrainer_mobile.features.home.domain.ObserveHomeHistory
import com.example.pushuptrainer_mobile.features.preparation.navigateToPreparationScreen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

internal class HomeViewModel(
    private val entry: NavigationEntry,
    private val preparation: Destination?,
    private val router: Router,
    observeHomeHistory: ObserveHomeHistory,
) : ViewModel() {
    private val canPrepare = preparation != null
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
                canPrepare = canPrepare,
            )
        }
        .catch { emit(HomeUiState.Unavailable(canPrepare)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState.Loading(canPrepare))

    fun onEvent(event: HomeUiEvent): Boolean = when (event) {
        HomeUiEvent.PrepareClicked -> preparation?.let { router.navigateToPreparationScreen(entry) } ?: false
    }
}

private fun LocalDate.dayAndMonth() =
    "${day.toString().padStart(2, '0')}.${(month.ordinal + 1).toString().padStart(2, '0')}"
