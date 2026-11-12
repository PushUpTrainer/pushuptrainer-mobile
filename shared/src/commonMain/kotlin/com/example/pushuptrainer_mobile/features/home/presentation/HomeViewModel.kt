package com.example.pushuptrainer_mobile.features.home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.features.home.domain.usecase.ObserveHomeHistoryUseCase
import com.example.pushuptrainer_mobile.features.onboarding.navigateToOnboardingScreen
import com.example.pushuptrainer_mobile.features.preparation.navigateToPreparationScreen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal class HomeViewModel(
    private val observeHomeHistory: ObserveHomeHistoryUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val entry: NavigationEntry,
    private val mainEntry: NavigationEntry,
    private val router: Router,
    private val globalRouter: Router,
) : ViewModel() {
    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    private val _effects = Channel<HomeUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    private var historyJob: Job? = null

    init {
        observeHistory()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.DaySelected -> selectDay(event.epochDay)
            HomeUiEvent.PreviousWeekClicked -> moveWeek(-7)
            HomeUiEvent.NextWeekClicked -> moveWeek(7)
            HomeUiEvent.TodayClicked -> {
                savedStateHandle.remove<Long>(SelectedDayKey)
                observeHistory()
            }
            HomeUiEvent.ThemeToggleClicked -> viewModelScope.launch { _effects.send(HomeUiEffect.ToggleTheme) }
            HomeUiEvent.OnboardingClicked -> {
                if (router.isCurrent(entry)) globalRouter.navigateToOnboardingScreen(mainEntry)
            }
            HomeUiEvent.PrepareClicked -> router.navigateToPreparationScreen(entry)
            HomeUiEvent.RetryClicked -> observeHistory()
        }
    }

    private fun selectDay(epochDay: Long) {
        val content = _state.value as? HomeUiState.Content ?: return
        if (epochDay > content.todayEpochDay) return
        savedStateHandle[SelectedDayKey] = epochDay.takeUnless { it == content.todayEpochDay }
        observeHistory()
    }

    private fun moveWeek(offset: Int) {
        val content = _state.value as? HomeUiState.Content ?: return
        if (offset > 0 && !content.week.canGoForward) return
        val selected = savedStateHandle.get<Long>(SelectedDayKey) ?: content.todayEpochDay
        selectDay(minOf(selected + offset, content.todayEpochDay))
    }

    private fun observeHistory() {
        historyJob?.cancel()
        val selectedDate = savedStateHandle.get<Long>(SelectedDayKey)?.let(LocalDate::fromEpochDays)
        historyJob = viewModelScope.launch {
            try {
                observeHomeHistory(selectedDate).collect { history ->
                    _state.update { history.toUiState() }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _state.update { current ->
                    (current as? HomeUiState.Content)?.copy(historyUnavailable = true) ?: HomeUiState.Unavailable
                }
            }
        }
    }

    private companion object {
        const val SelectedDayKey = "home_selected_day"
    }
}
