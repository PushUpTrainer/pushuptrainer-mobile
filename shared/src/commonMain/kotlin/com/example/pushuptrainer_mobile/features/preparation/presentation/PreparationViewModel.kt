package com.example.pushuptrainer_mobile.features.preparation.presentation

import androidx.lifecycle.ViewModel
import com.example.pushuptrainer_mobile.core.uikit.components.goalSelectorCard.GoalSelectorUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val MIN_GOAL = 1
private const val MAX_GOAL = 100
private const val GOAL_STEP = 1
private const val DEFAULT_GOAL = 20

/**
 * ViewModel экрана подготовки.
 *
 * Хранит выбранный режим, цель тренировки и видимость подсказки «Как поставить телефон».
 * Запуск тренировки, навигация и переключение темы подключаются отдельными задачами.
 */
class PreparationViewModel : ViewModel() {

    private val _state = MutableStateFlow(initialPreparationState())
    val state: StateFlow<PreparationUiState> = _state.asStateFlow()

    fun onEvent(event: PreparationUiEvent) {
        when (event) {
            is PreparationUiEvent.ModeSelected -> selectMode(event.mode)
            PreparationUiEvent.GoalDecreaseClicked -> changeGoal(-GOAL_STEP)
            PreparationUiEvent.GoalIncreaseClicked -> changeGoal(GOAL_STEP)
            PreparationUiEvent.HowToPlacePhoneClicked -> setHintVisible(true)
            PreparationUiEvent.HowToPlacePhoneDismissed -> setHintVisible(false)
        }
    }

    private fun selectMode(mode: PreparationMode) {
        _state.update { it.copy(selectedMode = mode) }
    }

    private fun changeGoal(delta: Int) {
        _state.update { state ->
            val newGoal = (state.goal.goal + delta).coerceIn(MIN_GOAL, MAX_GOAL)
            state.copy(goal = goalModel(newGoal))
        }
    }

    private fun setHintVisible(isVisible: Boolean) {
        _state.update { it.copy(isHowToPlacePhoneVisible = isVisible) }
    }
}

/** Модель счётчика с уже посчитанной доступностью кнопок «−» / «+». */
private fun goalModel(goal: Int): GoalSelectorUiModel = GoalSelectorUiModel(
    goal = goal,
    canDecrease = goal > MIN_GOAL,
    canIncrease = goal < MAX_GOAL,
)

private fun initialPreparationState(): PreparationUiState = PreparationUiState(
    selectedMode = PreparationMode.WithGoal,
    goal = goalModel(DEFAULT_GOAL),
    isHowToPlacePhoneVisible = false,
)
