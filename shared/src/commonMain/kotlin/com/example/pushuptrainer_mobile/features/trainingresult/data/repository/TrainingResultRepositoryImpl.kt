package com.example.pushuptrainer_mobile.features.trainingresult.data.repository

import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSource
import com.example.pushuptrainer_mobile.features.trainingresult.domain.model.SavedTrainingResult
import com.example.pushuptrainer_mobile.features.trainingresult.domain.repository.TrainingResultRepository

internal class TrainingResultRepositoryImpl(
    private val source: WorkoutLocalDataSource,
) : TrainingResultRepository {
    override suspend fun findResult(workoutId: String): SavedTrainingResult? {
        val session = source.getSession(workoutId)?.session ?: return null
        return SavedTrainingResult(
            repetitions = session.repetitions,
            goal = session.goalRepetitions,
        )
    }
}
