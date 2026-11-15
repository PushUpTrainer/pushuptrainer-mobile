package com.example.pushuptrainer_mobile.features.home.domain.model

import kotlinx.datetime.LocalDate

internal data class HomeHistory(
    val week: List<LocalDate>,
    val today: LocalDate,
    val selectedDate: LocalDate,
    val workoutDays: Set<LocalDate>,
    val repetitions: Int,
    val workouts: Int,
    val streakDays: Int,
    val hasWorkoutToday: Boolean,
)
