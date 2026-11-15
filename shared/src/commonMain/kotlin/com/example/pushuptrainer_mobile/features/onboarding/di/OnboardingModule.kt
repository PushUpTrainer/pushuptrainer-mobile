package com.example.pushuptrainer_mobile.features.onboarding.di

import com.example.pushuptrainer_mobile.core.navigation.di.GlobalRouterContext
import com.example.pushuptrainer_mobile.features.onboarding.presentation.OnboardingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

internal val onboardingModule = module {
    viewModel { parameters ->
        OnboardingViewModel(
            savedStateHandle = get(),
            entry = parameters.get(),
            router = get(qualifier<GlobalRouterContext>()),
        )
    }
}
