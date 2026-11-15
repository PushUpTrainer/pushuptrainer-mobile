package com.example.pushuptrainer_mobile.features.workout.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class CompletedWorkout(
    val id: String,
    val startedAtEpochMs: Long,
    val durationMs: Long,
    val repetitions: Int,
    val goal: Int?,
    val errors: Map<String, Int>,
)
