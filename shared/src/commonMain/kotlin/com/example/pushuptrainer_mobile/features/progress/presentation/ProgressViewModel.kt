package com.example.pushuptrainer_mobile.features.progress.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.features.preparation.PreparationRoute
import com.example.pushuptrainer_mobile.features.preparation.navigateToPreparationScreen
import com.example.pushuptrainer_mobile.features.progress.domain.usecase.ObserveProgressUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

internal class ProgressViewModel(
    private val observeProgress: ObserveProgressUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val entry: NavigationEntry,
    private val router: Router,
) : ViewModel() {
    private val _state = MutableStateFlow<ProgressUiState>(ProgressUiState.Loading)
    val state = _state.asStateFlow()
    private val _effects = Channel<ProgressUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()
    private var historyJob: Job? = null

    init { observeHistory() }

    fun onEvent(event: ProgressUiEvent) {
        when (event) {
            is ProgressUiEvent.DaySelected -> selectDay(event.epochDay)
            ProgressUiEvent.PreviousMonthClicked -> moveMonth(-1)
            ProgressUiEvent.NextMonthClicked -> moveMonth(1)
            ProgressUiEvent.TodayClicked -> {
                savedStateHandle.remove<Long>(SELECTED_DAY_KEY)
                observeHistory()
            }
            ProgressUiEvent.RetryClicked -> observeHistory()
            ProgressUiEvent.PrepareClicked -> {
                if (!router.returnTo(entry, PreparationRoute)) router.navigateToPreparationScreen(entry)
            }
            ProgressUiEvent.ThemeToggleClicked -> viewModelScope.launch { _effects.send(ProgressUiEffect.ToggleTheme) }
        }
    }

    private fun selectDay(epochDay: Long) {
        val content = state.value as? ProgressUiState.Content ?: return
        if (epochDay > content.todayEpochDay) return
        savedStateHandle[SELECTED_DAY_KEY] = epochDay.takeUnless { it == content.todayEpochDay }
        observeHistory()
    }

    private fun moveMonth(offset: Int) {
        val content = state.value as? ProgressUiState.Content ?: return
        if (offset > 0 && !content.calendar.canGoForward) return
        val day = LocalDate.fromEpochDays(content.selectedEpochDay).plus(offset, DateTimeUnit.MONTH)
        selectDay(minOf(day.toEpochDays(), content.todayEpochDay))
    }

    private fun observeHistory() {
        historyJob?.cancel()
        val selected = savedStateHandle.get<Long>(SELECTED_DAY_KEY)?.let(LocalDate::fromEpochDays)
        historyJob = viewModelScope.launch {
            try {
                observeProgress(selected).collect { _state.value = it.toUiState() }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _state.value = ProgressUiState.Unavailable
            }
        }
    }

    private companion object {
        const val SELECTED_DAY_KEY = "progress.selected_day"
    }
}
