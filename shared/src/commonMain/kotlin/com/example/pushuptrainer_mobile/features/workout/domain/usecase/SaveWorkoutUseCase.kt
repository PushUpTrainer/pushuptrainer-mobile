package com.example.pushuptrainer_mobile.features.workout.domain.usecase

import com.example.pushuptrainer_mobile.features.workout.domain.model.CompletedWorkout
import com.example.pushuptrainer_mobile.features.workout.domain.repository.WorkoutRepository

internal class SaveWorkoutUseCase(private val repository: WorkoutRepository) {
    suspend operator fun invoke(workout: CompletedWorkout) = repository.save(workout)
}
