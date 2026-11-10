package com.example.pushuptrainer_mobile.domain.workout

import kotlinx.coroutines.flow.Flow

internal data class SavedWorkout(
    val startedAtEpochMs: Long,
    val repetitions: Int,
    val goal: Int?,
)

internal interface WorkoutHistoryRepository {
    fun observe(): Flow<List<SavedWorkout>>
    suspend fun find(workoutId: String): SavedWorkout?
}
