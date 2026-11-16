package com.example.pushuptrainer_mobile.features.workout.domain.repository

import com.example.pushuptrainer_mobile.features.workout.domain.model.CompletedWorkout

internal interface WorkoutRepository {
    suspend fun save(workout: CompletedWorkout)
}
