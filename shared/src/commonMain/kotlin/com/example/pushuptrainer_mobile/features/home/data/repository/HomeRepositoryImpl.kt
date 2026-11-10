package com.example.pushuptrainer_mobile.features.home.data.repository

import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSource
import com.example.pushuptrainer_mobile.features.home.domain.model.HomeWorkout
import com.example.pushuptrainer_mobile.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class HomeRepositoryImpl(
    private val source: WorkoutLocalDataSource,
) : HomeRepository {
    override fun observeWorkouts(): Flow<List<HomeWorkout>> = source.observeSessions().map { workouts ->
        workouts.map { workout ->
            HomeWorkout(
                startedAtEpochMs = workout.session.startedAtEpochMs,
                repetitions = workout.session.repetitions,
            )
        }
    }
}
