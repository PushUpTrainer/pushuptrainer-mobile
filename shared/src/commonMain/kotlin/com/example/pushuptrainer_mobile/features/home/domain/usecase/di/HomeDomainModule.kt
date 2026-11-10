package com.example.pushuptrainer_mobile.features.home.domain.usecase.di

import com.example.pushuptrainer_mobile.features.home.domain.usecase.ObserveHomeHistoryUseCase
import org.koin.dsl.module

internal val homeDomainModule = module {
    factory { ObserveHomeHistoryUseCase(get()) }
}
