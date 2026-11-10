package com.example.pushuptrainer_mobile.features.trainingresult.domain.repository

import com.example.pushuptrainer_mobile.features.trainingresult.domain.model.SavedTrainingResult

internal interface TrainingResultRepository {
    suspend fun findResult(workoutId: String): SavedTrainingResult?
}
