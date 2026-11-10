package com.example.pushuptrainer_mobile.features.home.domain.di

import com.example.pushuptrainer_mobile.features.home.domain.ObserveHomeHistory
import org.koin.dsl.module

internal val homeDomainModule = module {
    factory { ObserveHomeHistory(get()) }
}
