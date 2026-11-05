package com.example.pushuptrainer_mobile.features.workout.presentation

sealed interface WorkoutCounterUiModel {
    val repetitions: String

    data class Free(override val repetitions: String) : WorkoutCounterUiModel

    data class Goal(
        override val repetitions: String,
        val target: String,
    ) : WorkoutCounterUiModel
}
