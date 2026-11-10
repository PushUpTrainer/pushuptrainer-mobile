package com.example.pushuptrainer_mobile.features.trainingresult.domain.usecase

import com.example.pushuptrainer_mobile.features.trainingresult.domain.model.SavedTrainingResult
import com.example.pushuptrainer_mobile.features.trainingresult.domain.repository.TrainingResultRepository

internal class GetTrainingResultUseCase(private val repository: TrainingResultRepository) {
    suspend operator fun invoke(workoutId: String): SavedTrainingResult? = repository.findResult(workoutId)
}
