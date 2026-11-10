package com.example.pushuptrainer_mobile.features.trainingresult.domain

import com.example.pushuptrainer_mobile.domain.workout.WorkoutHistoryRepository

internal class GetTrainingResult(private val repository: WorkoutHistoryRepository) {
    suspend operator fun invoke(workoutId: String) = repository.find(workoutId)
}
