package com.example.pushuptrainer_mobile.domain.di

import com.example.pushuptrainer_mobile.features.home.domain.di.homeDomainModule
import com.example.pushuptrainer_mobile.features.trainingresult.domain.di.trainingResultDomainModule
import org.koin.dsl.module

internal val sharedDomainModules = module {
    includes(homeDomainModule, trainingResultDomainModule)
}
