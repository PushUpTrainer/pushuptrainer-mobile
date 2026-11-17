package com.example.pushuptrainer_mobile.features.progress.domain.repository

import com.example.pushuptrainer_mobile.features.progress.domain.model.ProgressWorkout
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeWorkouts(): Flow<List<ProgressWorkout>>
}
