package com.example.pushuptrainer_mobile.features.home.domain.model

import kotlinx.datetime.LocalDate

internal data class HomeHistory(
    val week: List<LocalDate>,
    val today: LocalDate,
    val workoutDays: Set<LocalDate>,
    val repetitionsToday: Int,
    val workoutsToday: Int,
)
