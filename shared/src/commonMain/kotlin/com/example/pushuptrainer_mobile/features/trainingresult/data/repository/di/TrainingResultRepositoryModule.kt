package com.example.pushuptrainer_mobile.features.trainingresult.data.repository.di

import com.example.pushuptrainer_mobile.features.trainingresult.data.repository.TrainingResultRepositoryImpl
import com.example.pushuptrainer_mobile.features.trainingresult.domain.repository.TrainingResultRepository
import org.koin.dsl.module

internal val trainingResultRepositoryModule = module {
    single<TrainingResultRepository> { TrainingResultRepositoryImpl(get()) }
}
