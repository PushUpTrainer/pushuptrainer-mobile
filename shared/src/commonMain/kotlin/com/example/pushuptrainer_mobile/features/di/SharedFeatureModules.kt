package com.example.pushuptrainer_mobile.features.di

import com.example.pushuptrainer_mobile.features.home.di.homeModule
import com.example.pushuptrainer_mobile.features.main.di.mainModule
import com.example.pushuptrainer_mobile.features.onboarding.di.onboardingModule
import com.example.pushuptrainer_mobile.features.preparation.di.preparationModule
import com.example.pushuptrainer_mobile.features.trainingresult.di.trainingResultModule
import org.koin.dsl.module

internal val sharedFeatureModules = module {
    includes(onboardingModule, mainModule, homeModule, preparationModule, trainingResultModule)
}
