package com.example.pushuptrainer_mobile.features.trainingresult.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.StreakCardUiModel

/** Тестовые данные для Preview экрана результата (значения из макета Figma). */
internal object TrainingResultPreviewData {

    private val streak = StreakCardUiModel(daysInRow = 3)

    val noGoal = TrainingResultUiState.Content(
        summary = TrainingResultSummaryUiModel(
            repetitions = 16,
            outcome = TrainingOutcomeUiModel.NoGoal,
        ),
        streak = streak,
    )

    val goalReached = TrainingResultUiState.Content(
        summary = TrainingResultSummaryUiModel(
            repetitions = 16,
            outcome = TrainingOutcomeUiModel.GoalReached(goal = 16),
        ),
        streak = streak,
    )

    val stoppedEarly = TrainingResultUiState.Content(
        summary = TrainingResultSummaryUiModel(
            repetitions = 16,
            outcome = TrainingOutcomeUiModel.StoppedEarly(goal = 20),
        ),
        streak = streak,
    )
}
