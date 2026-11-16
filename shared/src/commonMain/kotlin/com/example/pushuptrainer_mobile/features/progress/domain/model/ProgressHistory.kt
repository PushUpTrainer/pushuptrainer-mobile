package com.example.pushuptrainer_mobile.features.progress.domain.model

import kotlinx.datetime.LocalDate

data class ProgressWorkout(val startedAtEpochMs: Long, val repetitions: Int)

data class ProgressHistory(
    val today: LocalDate,
    val selectedDate: LocalDate,
    val workoutDays: Set<LocalDate>,
    val dayWorkouts: Int,
    val dayRepetitions: Int,
    val totalRepetitions: Long,
    val bestWorkout: Int,
    val totalWorkouts: Int,
    val averageRepetitions: Double,
    val streakDays: Int,
)
