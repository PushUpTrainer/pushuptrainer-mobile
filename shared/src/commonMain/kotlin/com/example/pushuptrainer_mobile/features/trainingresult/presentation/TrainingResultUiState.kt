package com.example.pushuptrainer_mobile.features.trainingresult.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.trainingResultSummaryCard.TrainingResultSummaryUiModel

/** Состояние экрана результата тренировки. */
sealed interface TrainingResultUiState {

    /** Итог тренировки ещё подготавливается. */
    data object Loading : TrainingResultUiState

    data object Unavailable : TrainingResultUiState

    /** Итог тренировки готов к отображению. */
    data class Content(
        val summary: TrainingResultSummaryUiModel,
        val streak: StreakCardUiModel? = null,
        val canStartNew: Boolean = true,
    ) : TrainingResultUiState
}
