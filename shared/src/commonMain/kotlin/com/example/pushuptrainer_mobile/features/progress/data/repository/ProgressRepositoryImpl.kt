package com.example.pushuptrainer_mobile.features.progress.data.repository

import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSource
import com.example.pushuptrainer_mobile.features.progress.domain.model.ProgressWorkout
import com.example.pushuptrainer_mobile.features.progress.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.map

internal class ProgressRepositoryImpl(private val source: WorkoutLocalDataSource) : ProgressRepository {
    override fun observeWorkouts() = source.observeSessions().map { sessions ->
        sessions.map { ProgressWorkout(it.session.startedAtEpochMs, it.session.repetitions) }
    }
}
