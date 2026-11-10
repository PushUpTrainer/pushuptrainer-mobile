package com.example.pushuptrainer_mobile.domain.di

import com.example.pushuptrainer_mobile.features.home.domain.usecase.di.homeDomainModule
import com.example.pushuptrainer_mobile.features.trainingresult.domain.usecase.di.trainingResultDomainModule
import org.koin.dsl.module

internal val sharedDomainModules = module {
    includes(homeDomainModule, trainingResultDomainModule)
}
