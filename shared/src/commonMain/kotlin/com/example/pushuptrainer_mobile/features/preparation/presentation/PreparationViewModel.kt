package com.example.pushuptrainer_mobile.features.preparation.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.core.uikit.components.goalSelectorCard.GoalSelectorUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationMode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MIN_GOAL = 1
private const val MAX_GOAL = 100
private const val DEFAULT_GOAL = 20
private const val MODE_KEY = "preparation.mode"
private const val GOAL_KEY = "preparation.goal"
private const val HINT_KEY = "preparation.hint"

internal class PreparationViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow(
        PreparationUiState(
            selectedMode = PreparationMode.entries.firstOrNull {
                it.name == savedStateHandle.get<String>(MODE_KEY)
            } ?: PreparationMode.WithGoal,
            goal = goalModel(savedStateHandle.get<Int>(GOAL_KEY) ?: DEFAULT_GOAL),
            isHowToPlacePhoneVisible = savedStateHandle[HINT_KEY] ?: false,
        ),
    )
    val state: StateFlow<PreparationUiState> = _state.asStateFlow()

    private val _effects = Channel<PreparationUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: PreparationUiEvent) {
        when (event) {
            is PreparationUiEvent.ModeSelected -> selectMode(event.mode)
            PreparationUiEvent.GoalDecreaseClicked -> changeGoal(-1)
            PreparationUiEvent.GoalIncreaseClicked -> changeGoal(1)
            PreparationUiEvent.HowToPlacePhoneClicked -> setHintVisible(true)
            PreparationUiEvent.HowToPlacePhoneDismissed -> setHintVisible(false)
            PreparationUiEvent.ThemeToggleClicked -> viewModelScope.launch {
                _effects.send(PreparationUiEffect.ToggleTheme)
            }
            PreparationUiEvent.StartTrainingClicked -> Unit
        }
    }

    private fun selectMode(mode: PreparationMode) {
        savedStateHandle[MODE_KEY] = mode.name
        _state.update { it.copy(selectedMode = mode) }
    }

    private fun changeGoal(delta: Int) {
        if (_state.value.selectedMode != PreparationMode.WithGoal) return
        val goal = goalModel(_state.value.goal.goal + delta)
        savedStateHandle[GOAL_KEY] = goal.goal
        _state.update { it.copy(goal = goal) }
    }

    private fun setHintVisible(isVisible: Boolean) {
        savedStateHandle[HINT_KEY] = isVisible
        _state.update { it.copy(isHowToPlacePhoneVisible = isVisible) }
    }
}

private fun goalModel(value: Int): GoalSelectorUiModel {
    val goal = value.coerceIn(MIN_GOAL, MAX_GOAL)
    return GoalSelectorUiModel(
        goal = goal,
        canDecrease = goal > MIN_GOAL,
        canIncrease = goal < MAX_GOAL,
    )
}
