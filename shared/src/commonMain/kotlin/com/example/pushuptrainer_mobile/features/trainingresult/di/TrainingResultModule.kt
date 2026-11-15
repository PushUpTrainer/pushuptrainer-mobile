package com.example.pushuptrainer_mobile.features.trainingresult.di

import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.features.main.di.MainRouterContext
import com.example.pushuptrainer_mobile.features.trainingresult.data.repository.di.trainingResultRepositoryModule
import com.example.pushuptrainer_mobile.features.trainingresult.domain.usecase.di.trainingResultDomainModule
import com.example.pushuptrainer_mobile.features.trainingresult.presentation.TrainingResultViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

internal val trainingResultModule = module {
    includes(trainingResultRepositoryModule, trainingResultDomainModule)
    viewModel { parameters ->
        TrainingResultViewModel(
            entry = parameters.get(),
            workoutId = parameters.get(),
            preparation = parameters.getOrNull<Destination>(),
            router = get(qualifier<MainRouterContext>()),
            getTrainingResult = get(),
        )
    }
}
