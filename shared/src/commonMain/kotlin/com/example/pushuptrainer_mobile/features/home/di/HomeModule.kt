package com.example.pushuptrainer_mobile.features.home.di

import com.example.pushuptrainer_mobile.core.navigation.di.GlobalRouterContext
import com.example.pushuptrainer_mobile.features.home.data.repository.di.homeRepositoryModule
import com.example.pushuptrainer_mobile.features.home.domain.usecase.di.homeDomainModule
import com.example.pushuptrainer_mobile.features.home.presentation.HomeViewModel
import com.example.pushuptrainer_mobile.features.main.di.MainRouterContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

internal val homeModule = module {
    includes(homeRepositoryModule, homeDomainModule)
    viewModel { parameters ->
        HomeViewModel(
            observeHomeHistory = get(),
            savedStateHandle = get(),
            entry = parameters.get(0),
            mainEntry = parameters.get(1),
            router = get(qualifier<MainRouterContext>()),
            globalRouter = get(qualifier<GlobalRouterContext>()),
        )
    }
}
