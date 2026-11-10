package com.example.pushuptrainer_mobile.data.workout.di

import com.example.pushuptrainer_mobile.data.workout.LocalWorkoutHistoryRepository
import com.example.pushuptrainer_mobile.domain.workout.WorkoutHistoryRepository
import org.koin.dsl.module

internal val workoutHistoryModule = module {
    single<WorkoutHistoryRepository> { LocalWorkoutHistoryRepository(get()) }
}
