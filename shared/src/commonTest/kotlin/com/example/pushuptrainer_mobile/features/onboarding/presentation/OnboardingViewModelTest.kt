package com.example.pushuptrainer_mobile.features.onboarding.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation3.runtime.NavKey
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.StackRouter
import com.example.pushuptrainer_mobile.core.navigation.newEntry
import com.example.pushuptrainer_mobile.features.main.MainRoute
import com.example.pushuptrainer_mobile.features.onboarding.OnboardingRoute
import com.example.pushuptrainer_mobile.features.onboarding.navigateToOnboardingScreen
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Инструкция открывается с главного экрана, листается и закрывается обратно на главный. */
class OnboardingViewModelTest {

    @Test
    fun stepsMoveWithinBoundsAndFinishReturnsToMainScreen() {
        val fixture = Fixture()
        with(fixture) {
            assertEquals(OnboardingStep.Modes, viewModel.state.value.step)
            viewModel.onEvent(OnboardingUiEvent.BackClicked)
            assertEquals(OnboardingStep.Modes, viewModel.state.value.step)

            repeat(OnboardingStep.entries.size + 1) { viewModel.onEvent(OnboardingUiEvent.NextClicked) }
            assertEquals(OnboardingStep.Appearance, viewModel.state.value.step)
            assertTrue(viewModel.state.value.isLastStep)

            viewModel.onEvent(OnboardingUiEvent.FinishClicked)
            assertEquals(listOf<NavKey>(mainEntry), globalStack)
        }
    }

    @Test
    fun closeReturnsToMainScreenAndInstructionCanBeOpenedAgain() {
        val fixture = Fixture()
        with(fixture) {
            viewModel.onEvent(OnboardingUiEvent.StepSwiped(OnboardingStep.Progress))
            viewModel.onEvent(OnboardingUiEvent.CloseClicked)
            assertEquals(listOf<NavKey>(mainEntry), globalStack)

            assertTrue(globalRouter.navigateToOnboardingScreen(mainEntry))
            assertEquals(OnboardingRoute, (globalStack.last() as NavigationEntry).destination)
        }
    }

    @Test
    fun secondOpenWhileInstructionIsShownIsIgnored() {
        val fixture = Fixture()
        with(fixture) {
            assertFalse(globalRouter.navigateToOnboardingScreen(mainEntry))
            assertEquals(2, globalStack.size)
        }
    }

    @Test
    fun stepIsRestoredFromSavedState() {
        val savedState = SavedStateHandle()
        Fixture(savedState).viewModel.onEvent(OnboardingUiEvent.StepSwiped(OnboardingStep.Progress))
        assertEquals(OnboardingStep.Progress, Fixture(savedState).viewModel.state.value.step)
    }

    private class Fixture(savedState: SavedStateHandle = SavedStateHandle()) {
        val mainEntry = MainRoute.newEntry()
        val globalStack = mutableListOf<NavKey>(mainEntry)
        val globalRouter = StackRouter().also { it.attach(globalStack) { true } }
        val onboardingEntry: NavigationEntry

        init {
            check(globalRouter.navigateToOnboardingScreen(mainEntry))
            onboardingEntry = globalStack.last() as NavigationEntry
        }

        val viewModel = OnboardingViewModel(savedState, onboardingEntry, globalRouter)
    }
}
