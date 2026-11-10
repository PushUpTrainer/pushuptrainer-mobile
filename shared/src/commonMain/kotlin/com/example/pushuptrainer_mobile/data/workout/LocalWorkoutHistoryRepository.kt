package com.example.pushuptrainer_mobile.data.workout

import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutSessionWithErrors
import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSource
import com.example.pushuptrainer_mobile.domain.workout.SavedWorkout
import com.example.pushuptrainer_mobile.domain.workout.WorkoutHistoryRepository
import kotlinx.coroutines.flow.map

internal class LocalWorkoutHistoryRepository(
    private val source: WorkoutLocalDataSource,
) : WorkoutHistoryRepository {
    override fun observe() = source.observeSessions().map { workouts -> workouts.map { it.toSummary() } }

    override suspend fun find(workoutId: String): SavedWorkout? =
        source.getSession(workoutId)?.toSummary()
}

private fun WorkoutSessionWithErrors.toSummary() = SavedWorkout(
    startedAtEpochMs = session.startedAtEpochMs,
    repetitions = session.repetitions,
    goal = session.goalRepetitions,
)
