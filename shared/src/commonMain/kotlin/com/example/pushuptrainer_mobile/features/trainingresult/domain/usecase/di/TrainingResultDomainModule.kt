package com.example.pushuptrainer_mobile.features.trainingresult.domain.usecase.di

import com.example.pushuptrainer_mobile.features.trainingresult.domain.usecase.GetTrainingResultUseCase
import org.koin.dsl.module

internal val trainingResultDomainModule = module {
    factory { GetTrainingResultUseCase(get()) }
}
