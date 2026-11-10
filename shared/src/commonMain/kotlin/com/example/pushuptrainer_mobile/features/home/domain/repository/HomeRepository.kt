package com.example.pushuptrainer_mobile.features.home.domain.repository

import com.example.pushuptrainer_mobile.features.home.domain.model.HomeWorkout
import kotlinx.coroutines.flow.Flow

internal interface HomeRepository {
    fun observeWorkouts(): Flow<List<HomeWorkout>>
}
