package com.example.pushuptrainer_mobile.features.onboarding.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val entry: NavigationEntry,
    private val router: Router,
) : ViewModel() {

    private val _state = MutableStateFlow(
        OnboardingUiState(step = OnboardingStep.entries[savedStateHandle.get<Int>("onboarding_step") ?: 0]),
    )
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.NextClicked -> moveBy(1)
            OnboardingUiEvent.BackClicked -> moveBy(-1)
            is OnboardingUiEvent.StepSwiped -> selectStep(event.step)
            OnboardingUiEvent.FinishClicked -> router.back(entry)
            OnboardingUiEvent.CloseClicked -> router.back(entry)
        }
    }

    private fun moveBy(offset: Int) {
        val steps = OnboardingStep.entries
        val index = (_state.value.step.ordinal + offset).coerceIn(0, steps.lastIndex)
        selectStep(steps[index])
    }

    private fun selectStep(step: OnboardingStep) {
        savedStateHandle["onboarding_step"] = step.ordinal
        _state.update { current -> current.copy(step = step) }
    }
}
