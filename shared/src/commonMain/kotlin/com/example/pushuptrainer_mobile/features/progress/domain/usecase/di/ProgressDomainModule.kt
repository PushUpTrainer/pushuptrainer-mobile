package com.example.pushuptrainer_mobile.features.progress.domain.usecase.di

import com.example.pushuptrainer_mobile.features.progress.domain.usecase.ObserveProgressUseCase
import org.koin.dsl.module

internal val progressDomainModule = module {
    factory { ObserveProgressUseCase(get()) }
}
