package com.example.pushuptrainer_mobile.data.di

import com.example.pushuptrainer_mobile.data.local.di.sharedLocalModule
import com.example.pushuptrainer_mobile.features.home.data.repository.di.homeRepositoryModule
import com.example.pushuptrainer_mobile.features.trainingresult.data.repository.di.trainingResultRepositoryModule
import org.koin.dsl.module

val sharedDataModules = module {
    includes(sharedLocalModule, homeRepositoryModule, trainingResultRepositoryModule)
}
