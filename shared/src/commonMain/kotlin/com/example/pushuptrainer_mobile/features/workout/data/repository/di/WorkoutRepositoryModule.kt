package com.example.pushuptrainer_mobile.features.workout.data.repository.di

import com.example.pushuptrainer_mobile.features.workout.data.repository.WorkoutRepositoryImpl
import com.example.pushuptrainer_mobile.features.workout.domain.repository.WorkoutRepository
import org.koin.dsl.module

internal val workoutRepositoryModule = module {
    single<WorkoutRepository> { WorkoutRepositoryImpl(get()) }
}
