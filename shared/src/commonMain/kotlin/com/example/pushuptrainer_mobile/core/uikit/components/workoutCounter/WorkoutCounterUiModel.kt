package com.example.pushuptrainer_mobile.core.uikit.components.workoutCounter

sealed interface WorkoutCounterUiModel {
    val repetitions: String

    data class Free(override val repetitions: String) : WorkoutCounterUiModel

    data class Goal(
        override val repetitions: String,
        val target: String,
    ) : WorkoutCounterUiModel
}
