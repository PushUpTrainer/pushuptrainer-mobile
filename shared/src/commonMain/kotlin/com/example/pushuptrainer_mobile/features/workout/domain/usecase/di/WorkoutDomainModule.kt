package com.example.pushuptrainer_mobile.features.workout.domain.usecase.di

import com.example.pushuptrainer_mobile.features.workout.domain.usecase.SaveWorkoutUseCase
import com.example.pushuptrainer_mobile.features.workout.domain.usecase.WorkoutSessionUseCase
import org.koin.dsl.module

internal val workoutDomainModule = module {
    factory { SaveWorkoutUseCase(get()) }
    factory { parameters -> WorkoutSessionUseCase(goal = parameters.getOrNull<Int>()) }
}
