package com.example.pushuptrainer_mobile.features.trainingresult.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel экрана результата тренировки.
 *
 * Пока это каркас: загрузка итогов тренировки, навигация и переключение темы
 * подключаются отдельными задачами.
 */
class TrainingResultViewModel : ViewModel() {

    private val _state = MutableStateFlow<TrainingResultUiState>(TrainingResultUiState.Loading)
    val state: StateFlow<TrainingResultUiState> = _state.asStateFlow()

    fun onEvent(event: TrainingResultUiEvent) {
        when (event) {
            TrainingResultUiEvent.StartNewTrainingClicked -> Unit
            TrainingResultUiEvent.GoToMainClicked -> Unit
            TrainingResultUiEvent.ThemeToggleClicked -> Unit
            is TrainingResultUiEvent.BottomBarDestinationClicked -> Unit
        }
    }
}
