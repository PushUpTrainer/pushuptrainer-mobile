package com.example.pushuptrainer_mobile.features.progress

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.features.progress.domain.model.ProgressWorkout
import com.example.pushuptrainer_mobile.features.progress.domain.repository.ProgressRepository
import com.example.pushuptrainer_mobile.features.progress.domain.usecase.ObserveProgressUseCase
import com.example.pushuptrainer_mobile.features.progress.presentation.ProgressUiEvent
import com.example.pushuptrainer_mobile.features.progress.presentation.ProgressUiState
import com.example.pushuptrainer_mobile.features.progress.presentation.ProgressViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
abstract class ProgressViewModelTest {
    @Test
    fun liveHistoryMonthNavigationAndRestorationStayConsistent() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val today = LocalDate(2026, 11, 16)
        val previous = LocalDate(2026, 10, 16)
        val workouts = MutableStateFlow(emptyList<ProgressWorkout>())
        val repository = object : ProgressRepository { override fun observeWorkouts() = workouts }
        val clock = object : Clock { override fun now() = Instant.parse("2026-11-16T12:00:00Z") }
        val useCase = ObserveProgressUseCase(repository, clock) { TimeZone.UTC }
        val handle = SavedStateHandle()
        val vm = ProgressViewModel(useCase, handle)
        try {
            runCurrent()
            assertEquals("0", (vm.state.value as ProgressUiState.Content).totalRepetitions)
            workouts.value = listOf(
                ProgressWorkout(today.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(), 8),
                ProgressWorkout(previous.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(), 12),
            )
            runCurrent()
            assertEquals(8, (vm.state.value as ProgressUiState.Content).day.repetitions)
            vm.onEvent(ProgressUiEvent.PreviousMonthClicked)
            runCurrent()
            assertEquals(12, (vm.state.value as ProgressUiState.Content).day.repetitions)
            val restored = ProgressViewModel(useCase, SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) }))
            try {
                runCurrent()
                assertEquals(previous.toEpochDays(), (restored.state.value as ProgressUiState.Content).selectedEpochDay)
            } finally { restored.viewModelScope.cancel() }
            vm.onEvent(ProgressUiEvent.TodayClicked)
            runCurrent()
            vm.onEvent(ProgressUiEvent.NextMonthClicked)
            vm.onEvent(ProgressUiEvent.DaySelected(today.toEpochDays() + 1))
            runCurrent()
            val content = vm.state.value as ProgressUiState.Content
            assertEquals(today.toEpochDays(), content.selectedEpochDay)
            assertEquals("20", content.totalRepetitions)
            assertEquals("12", content.bestWorkout)
        } finally {
            vm.viewModelScope.cancel()
            Dispatchers.resetMain()
        }
    }
}
