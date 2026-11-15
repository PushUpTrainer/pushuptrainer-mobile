package com.example.pushuptrainer_mobile.features.workout.data.repository

import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutErrorEntity
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutSessionEntity
import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSource
import com.example.pushuptrainer_mobile.features.workout.domain.model.CompletedWorkout
import com.example.pushuptrainer_mobile.features.workout.domain.repository.WorkoutRepository

internal class WorkoutRepositoryImpl(private val source: WorkoutLocalDataSource) : WorkoutRepository {
    override suspend fun save(workout: CompletedWorkout) {
        if (source.getSession(workout.id) != null) return
        source.save(
            WorkoutSessionEntity(
                id = workout.id,
                startedAtEpochMs = workout.startedAtEpochMs,
                durationMs = workout.durationMs,
                repetitions = workout.repetitions,
                goalRepetitions = workout.goal,
            ),
            workout.errors.map { (code, count) -> WorkoutErrorEntity(workout.id, code, count) },
        )
    }
}
