package com.example.pushuptrainer_mobile.features.onboarding.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel онбординга: переключает шаги кнопками и свайпом.
 *
 * Закрытие онбординга («Понятно» и крестик) — это переход на другой экран,
 * он подключается задачей навигации.
 */
class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState(step = OnboardingStep.entries.first()))
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.NextClicked -> moveBy(1)
            OnboardingUiEvent.BackClicked -> moveBy(-1)
            is OnboardingUiEvent.StepSwiped -> selectStep(event.step)
            OnboardingUiEvent.FinishClicked -> Unit
            OnboardingUiEvent.CloseClicked -> Unit
        }
    }

    private fun moveBy(offset: Int) {
        _state.update { current ->
            val steps = OnboardingStep.entries
            val index = (current.step.ordinal + offset).coerceIn(0, steps.lastIndex)
            current.copy(step = steps[index])
        }
    }

    private fun selectStep(step: OnboardingStep) {
        _state.update { current -> current.copy(step = step) }
    }
}
