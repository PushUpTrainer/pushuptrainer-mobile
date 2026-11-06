package com.example.pushuptrainer_mobile.core.uikit.components.trainingResultSummaryCard

/** Данные карточки с итогом тренировки. */
data class TrainingResultSummaryUiModel(
    val repetitions: Int,
    val outcome: TrainingOutcomeUiModel,
)

/**
 * Чем закончилась тренировка. Каждому варианту в макете соответствует свой экран:
 * 1.3.1 «Без цели», 1.3.2 «Выполненная цель», 1.3.3 «Досрочно».
 */
sealed interface TrainingOutcomeUiModel {

    /** Тренировка без цели: повторения считались без лимита. */
    data object NoGoal : TrainingOutcomeUiModel

    /** Цель достигнута. */
    data class GoalReached(val goal: Int) : TrainingOutcomeUiModel

    /** Тренировка с целью, остановленная до её выполнения. */
    data class StoppedEarly(val goal: Int) : TrainingOutcomeUiModel
}
