package com.example.pushuptrainer_mobile.features.preparation.presentation

import com.example.pushuptrainer_mobile.core.navigation.StackRouter
import com.example.pushuptrainer_mobile.core.navigation.newEntry
import com.example.pushuptrainer_mobile.features.preparation.PreparationRoute
import com.example.pushuptrainer_mobile.features.workout.WorkoutRoute
import com.example.pushuptrainer_mobile.features.main.presentation.MainViewModel
import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination
import org.koin.core.parameter.parametersOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationMode
import com.example.pushuptrainer_mobile.di.sharedModules
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.dsl.koinApplication
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class PreparationViewModelTest {
    @Test
    fun goalStopsAtBothBoundsAndCanMoveBackFromThem() {
        val viewModel = createViewModel(SavedStateHandle())
        assertEquals(20, viewModel.state.value.goal.goal)
        repeat(120) { viewModel.onEvent(PreparationUiEvent.GoalDecreaseClicked) }
        assertEquals(1, viewModel.state.value.goal.goal)
        assertFalse(viewModel.state.value.goal.canDecrease)
        assertTrue(viewModel.state.value.goal.canIncrease)
        viewModel.onEvent(PreparationUiEvent.GoalIncreaseClicked)
        assertEquals(2, viewModel.state.value.goal.goal)
        assertTrue(viewModel.state.value.goal.canDecrease)
        repeat(120) { viewModel.onEvent(PreparationUiEvent.GoalIncreaseClicked) }
        assertEquals(100, viewModel.state.value.goal.goal)
        assertTrue(viewModel.state.value.goal.canDecrease)
        assertFalse(viewModel.state.value.goal.canIncrease)
        viewModel.onEvent(PreparationUiEvent.GoalDecreaseClicked)
        assertEquals(99, viewModel.state.value.goal.goal)
        assertTrue(viewModel.state.value.goal.canIncrease)
        assertFalse(viewModel.state is MutableStateFlow<*>)
    }

    @Test
    fun modeGoalAndHintRestoreWithoutSharingMutableState() {
        val handle = SavedStateHandle()
        val viewModel = createViewModel(handle)
        repeat(5) { viewModel.onEvent(PreparationUiEvent.GoalIncreaseClicked) }
        viewModel.onEvent(PreparationUiEvent.ModeSelected(PreparationMode.WithoutGoal))
        viewModel.onEvent(PreparationUiEvent.GoalDecreaseClicked)
        viewModel.onEvent(PreparationUiEvent.GoalIncreaseClicked)
        viewModel.onEvent(PreparationUiEvent.HowToPlacePhoneClicked)
        assertEquals(25, viewModel.state.value.goal.goal)
        val restored = createViewModel(
            SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) }),
        )
        assertEquals(viewModel.state.value, restored.state.value)
        assertNotSame(viewModel.state, restored.state)
        restored.onEvent(PreparationUiEvent.HowToPlacePhoneDismissed)
        restored.onEvent(PreparationUiEvent.ModeSelected(PreparationMode.WithGoal))
        assertFalse(restored.state.value.isHowToPlacePhoneVisible)
        assertEquals(25, restored.state.value.goal.goal)
        assertTrue(viewModel.state.value.isHowToPlacePhoneVisible)
        assertEquals(PreparationMode.WithoutGoal, viewModel.state.value.selectedMode)
    }

    @Test
    fun invalidRestoredValuesUseSupportedModeAndGoalBounds() {
        for ((saved, expected) in listOf(Int.MIN_VALUE to 1, Int.MAX_VALUE to 100)) {
            val viewModel = createViewModel(
                SavedStateHandle(mapOf("preparation.mode" to "obsolete", "preparation.goal" to saved)),
            )
            assertEquals(PreparationMode.WithGoal, viewModel.state.value.selectedMode)
            assertEquals(expected, viewModel.state.value.goal.goal)
        }
    }

    @Test
    fun themeEventsAreConsumedOnceAndKeepPreparationState() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = createViewModel(SavedStateHandle())
        try {
            viewModel.onEvent(PreparationUiEvent.GoalIncreaseClicked)
            val state = viewModel.state.value
            val firstEffects = mutableListOf<PreparationUiEffect>()
            val firstCollector = launch { viewModel.effects.take(1).toList(firstEffects) }
            viewModel.onEvent(PreparationUiEvent.ThemeToggleClicked)
            runCurrent()
            firstCollector.join()
            assertEquals(listOf<PreparationUiEffect>(PreparationUiEffect.ToggleTheme), firstEffects)
            val nextEffects = mutableListOf<PreparationUiEffect>()
            val nextCollector = launch { viewModel.effects.take(1).toList(nextEffects) }
            runCurrent()
            assertTrue(nextEffects.isEmpty())
            viewModel.onEvent(PreparationUiEvent.ThemeToggleClicked)
            runCurrent()
            nextCollector.join()
            assertEquals(listOf<PreparationUiEffect>(PreparationUiEffect.ToggleTheme), nextEffects)
            assertEquals(state, viewModel.state.value)
        } finally {
            viewModel.viewModelScope.cancel()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun sharedGraphResolvesPreparationWithHostSavedState() {
        val application = koinApplication {
            modules(sharedModules, module { factory { SavedStateHandle() } })
        }
        try {
            val first = application.koin.get<PreparationViewModel> { parametersOf(PreparationRoute.newEntry()) }
            val second = application.koin.get<PreparationViewModel> { parametersOf(PreparationRoute.newEntry()) }
            first.onEvent(PreparationUiEvent.HowToPlacePhoneClicked)
            assertTrue(first.state.value.isHowToPlacePhoneVisible)
            assertFalse(second.state.value.isHowToPlacePhoneVisible)
            assertNotSame(first, second)
        } finally {
            application.close()
        }
    }
    @Test
    fun startPassesSelectedGoalAndDoesNotDuplicateWorkout() {
        val entry = PreparationRoute.newEntry()
        val stack = mutableListOf<androidx.navigation3.runtime.NavKey>(entry)
        val router = StackRouter().also { it.attach(stack) { true } }
        val viewModel = PreparationViewModel(SavedStateHandle(), router, entry)
        viewModel.onEvent(PreparationUiEvent.GoalIncreaseClicked)
        repeat(2) { viewModel.onEvent(PreparationUiEvent.StartTrainingClicked) }
        assertEquals(2, stack.size)
        val workout = stack.last() as com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
        assertEquals(WorkoutRoute(21), workout.destination)
        assertEquals(BottomNavigationDestination.Trainer, MainViewModel(router).state(workout).selected)
        router.back(workout)
        viewModel.onEvent(PreparationUiEvent.ModeSelected(PreparationMode.WithoutGoal))
        viewModel.onEvent(PreparationUiEvent.StartTrainingClicked)
        val freeWorkout = stack.last() as com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
        assertEquals(WorkoutRoute(null), freeWorkout.destination)
        assertEquals(BottomNavigationDestination.Trainer, MainViewModel(router).state(freeWorkout).selected)
    }

    private fun createViewModel(handle: SavedStateHandle) =
        PreparationViewModel(handle, StackRouter(), PreparationRoute.newEntry())

}
