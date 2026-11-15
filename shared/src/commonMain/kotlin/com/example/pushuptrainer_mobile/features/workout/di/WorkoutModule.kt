package com.example.pushuptrainer_mobile.features.workout.di

import com.example.pushuptrainer_mobile.features.main.di.MainRouterContext
import com.example.pushuptrainer_mobile.features.workout.data.repository.di.workoutRepositoryModule
import com.example.pushuptrainer_mobile.features.workout.domain.usecase.di.workoutDomainModule
import com.example.pushuptrainer_mobile.features.workout.presentation.WorkoutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

internal val workoutModule = module {
    includes(workoutRepositoryModule, workoutDomainModule)
    viewModel { parameters ->
        WorkoutViewModel(
            entry = parameters.get(),
            goal = parameters.getOrNull<Int>(),
            savedStateHandle = get(),
            router = get(qualifier<MainRouterContext>()),
            session = get { parametersOf(parameters.getOrNull<Int>()) },
            saveWorkout = get(),
        )
    }
}
