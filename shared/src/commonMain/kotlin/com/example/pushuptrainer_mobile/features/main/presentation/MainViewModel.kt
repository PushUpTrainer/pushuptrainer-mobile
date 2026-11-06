package com.example.pushuptrainer_mobile.features.main.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel главного экрана.
 *
 * Пока это каркас: загрузка данных, навигация и переключение темы
 * подключаются отдельными задачами.
 */
class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    fun onEvent(event: MainUiEvent) {
        when (event) {
            MainUiEvent.PrepareClicked -> Unit
            MainUiEvent.HowItWorksClicked -> Unit
            MainUiEvent.ThemeToggleClicked -> Unit
            is MainUiEvent.BottomBarDestinationClicked -> Unit
        }
    }
}
