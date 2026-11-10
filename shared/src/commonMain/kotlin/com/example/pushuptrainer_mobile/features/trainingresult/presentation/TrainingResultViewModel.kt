package com.example.pushuptrainer_mobile.features.trainingresult.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.core.uikit.components.trainingResultSummaryCard.TrainingOutcomeUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.trainingResultSummaryCard.TrainingResultSummaryUiModel
import com.example.pushuptrainer_mobile.features.home.navigateToHomeScreen
import com.example.pushuptrainer_mobile.features.trainingresult.domain.usecase.GetTrainingResultUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class TrainingResultViewModel(
    private val entry: NavigationEntry,
    workoutId: String,
    private val preparation: Destination?,
    private val router: Router,
    getTrainingResult: GetTrainingResultUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<TrainingResultUiState>(TrainingResultUiState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = try {
                getTrainingResult(workoutId)?.let { result ->
                    val goal = result.goal
                    TrainingResultUiState.Content(
                        summary = TrainingResultSummaryUiModel(
                            repetitions = result.repetitions,
                            outcome = when {
                                goal == null -> TrainingOutcomeUiModel.NoGoal
                                result.repetitions >= goal -> TrainingOutcomeUiModel.GoalReached(goal)
                                else -> TrainingOutcomeUiModel.StoppedEarly(goal)
                            },
                        ),
                        canStartNew = preparation != null,
                    )
                } ?: TrainingResultUiState.Unavailable
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                TrainingResultUiState.Unavailable
            }
        }
    }

    fun onEvent(event: TrainingResultUiEvent): Boolean = when (event) {
        TrainingResultUiEvent.StartNewTrainingClicked -> preparation?.let { router.replace(entry, it) } ?: false
        TrainingResultUiEvent.GoToMainClicked -> router.navigateToHomeScreen(entry)
    }
}
