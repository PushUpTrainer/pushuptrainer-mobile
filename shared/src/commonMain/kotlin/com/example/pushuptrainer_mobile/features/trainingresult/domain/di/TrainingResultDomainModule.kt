package com.example.pushuptrainer_mobile.features.trainingresult.domain.di

import com.example.pushuptrainer_mobile.features.trainingresult.domain.GetTrainingResult
import org.koin.dsl.module

internal val trainingResultDomainModule = module {
    factory { GetTrainingResult(get()) }
}
