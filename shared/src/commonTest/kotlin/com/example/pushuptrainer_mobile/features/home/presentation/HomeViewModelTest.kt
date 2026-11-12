package com.example.pushuptrainer_mobile.features.home.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.example.pushuptrainer_mobile.core.navigation.StackRouter
import com.example.pushuptrainer_mobile.core.navigation.newEntry
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.features.home.HomeRoute
import com.example.pushuptrainer_mobile.features.home.data.repository.calculateHomeHistory
import com.example.pushuptrainer_mobile.features.home.domain.model.HomeWorkout
import com.example.pushuptrainer_mobile.features.home.domain.repository.HomeRepository
import com.example.pushuptrainer_mobile.features.home.domain.usecase.ObserveHomeHistoryUseCase
import com.example.pushuptrainer_mobile.features.main.MainRoute
import com.example.pushuptrainer_mobile.features.onboarding.OnboardingRoute
import com.example.pushuptrainer_mobile.features.preparation.PreparationRoute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val today = LocalDate(2026, 11, 12)
    private val zone = TimeZone.UTC

    @Test
    fun calendarSelectionSurvivesLiveUpdatesAndCanReturnToToday() = runTest {
        withHome {
            val yesterday = LocalDate(2026, 11, 11)
            viewModel.onEvent(HomeUiEvent.DaySelected(yesterday.toEpochDays()))
            runCurrent()
            assertEquals(yesterday.toEpochDays(), content().selectedEpochDay)
            assertEquals(TodaySummaryUiModel.Empty("11.11.2026"), content().today)
            repository.workouts.value = listOf(workout(yesterday, 8), workout(today, 10))
            runCurrent()
            assertEquals(yesterday.toEpochDays(), content().selectedEpochDay)
            assertEquals(TodaySummaryUiModel.Done(8, 1, "11.11.2026"), content().today)
            viewModel.onEvent(HomeUiEvent.DaySelected(today.toEpochDays() + 1))
            runCurrent()
            assertEquals(yesterday.toEpochDays(), content().selectedEpochDay)
            viewModel.onEvent(HomeUiEvent.TodayClicked)
            runCurrent()
            assertEquals(today.toEpochDays(), content().selectedEpochDay)
            assertEquals(TodaySummaryUiModel.Done(10, 1), content().today)
            assertFalse(viewModel.state is MutableStateFlow<*>)
        }
    }

    @Test
    fun weeksMoveInBothDirectionsAndSelectionRestores() = runTest {
        val selected = today.toEpochDays() - 7
        withHome(SavedStateHandle(mapOf("home_selected_day" to selected))) {
            assertEquals(selected, content().selectedEpochDay)
            viewModel.onEvent(HomeUiEvent.PreviousWeekClicked)
            viewModel.onEvent(HomeUiEvent.PreviousWeekClicked)
            runCurrent()
            assertEquals(selected - 14, content().selectedEpochDay)
            repeat(3) {
                viewModel.onEvent(HomeUiEvent.NextWeekClicked)
                runCurrent()
            }
            assertEquals(today.toEpochDays(), content().selectedEpochDay)
            assertFalse(content().week.canGoForward)
            viewModel.onEvent(HomeUiEvent.NextWeekClicked)
            runCurrent()
            assertEquals(today.toEpochDays(), content().selectedEpochDay)
        }
    }

    @Test
    fun failedLoadRetriesWithoutLosingSelectedDayOrPreviousContent() = runTest {
        withHome {
            repository.fail = true
            viewModel.onEvent(HomeUiEvent.PreviousWeekClicked)
            runCurrent()
            assertTrue(content().historyUnavailable)
            repository.fail = false
            viewModel.onEvent(HomeUiEvent.RetryClicked)
            runCurrent()
            assertFalse(content().historyUnavailable)
            assertEquals(today.toEpochDays() - 7, content().selectedEpochDay)
        }
    }

    @Test
    fun themeAndNavigationAreHandledThroughEventsWithoutDuplicateEntries() = runTest {
        withHome {
            val effects = mutableListOf<HomeUiEffect>()
            val collector = launch { viewModel.effects.take(2).toList(effects) }
            repeat(2) { viewModel.onEvent(HomeUiEvent.ThemeToggleClicked) }
            runCurrent()
            assertEquals(listOf<HomeUiEffect>(HomeUiEffect.ToggleTheme, HomeUiEffect.ToggleTheme), effects)
            collector.join()
            repeat(2) { viewModel.onEvent(HomeUiEvent.OnboardingClicked) }
            assertEquals(2, globalStack.size)
            assertEquals(OnboardingRoute, (globalStack.last() as com.example.pushuptrainer_mobile.core.navigation.NavigationEntry).destination)
            globalRouter.back(globalStack.last() as com.example.pushuptrainer_mobile.core.navigation.NavigationEntry)
            repeat(2) { viewModel.onEvent(HomeUiEvent.PrepareClicked) }
            assertEquals(2, mainStack.size)
            assertEquals(PreparationRoute, (mainStack.last() as com.example.pushuptrainer_mobile.core.navigation.NavigationEntry).destination)
        }
    }

    private suspend fun TestScope.withHome(
        savedState: SavedStateHandle = SavedStateHandle(),
        block: suspend Fixture.() -> Unit,
    ) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val fixture = Fixture(savedState)
        try {
            runCurrent()
            fixture.block()
        } finally {
            fixture.viewModel.viewModelScope.cancel()
            Dispatchers.resetMain()
        }
    }

    private inner class Fixture(savedState: SavedStateHandle) {
        val repository = HistoryRepository()
        val mainEntry = MainRoute.newEntry()
        val homeEntry = HomeRoute.newEntry()
        val globalStack = mutableListOf<NavKey>(mainEntry)
        val mainStack = mutableListOf<NavKey>(homeEntry)
        val globalRouter = StackRouter().also { it.attach(globalStack) { true } }
        val router = StackRouter().also { it.attach(mainStack) { globalRouter.isCurrent(mainEntry) } }
        val viewModel = HomeViewModel(
            ObserveHomeHistoryUseCase(repository), savedState, homeEntry, mainEntry, router, globalRouter,
        )
        fun content() = assertIs<HomeUiState.Content>(viewModel.state.value)
    }

    private inner class HistoryRepository : HomeRepository {
        val workouts = MutableStateFlow<List<HomeWorkout>>(emptyList())
        var fail = false
        override fun observeHistory(selectedDate: LocalDate?) = workouts.map {
            check(!fail)
            calculateHomeHistory(it, today, zone, selectedDate ?: today)
        }
    }

    private fun workout(day: LocalDate, repetitions: Int) =
        HomeWorkout(day.atStartOfDayIn(zone).toEpochMilliseconds(), repetitions)
}
