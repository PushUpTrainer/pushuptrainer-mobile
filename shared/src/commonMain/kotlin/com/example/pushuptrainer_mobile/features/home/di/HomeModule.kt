package com.example.pushuptrainer_mobile.features.home.di

import com.example.pushuptrainer_mobile.features.home.presentation.HomeViewModel
import com.example.pushuptrainer_mobile.features.main.di.MainRouterContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

internal val homeModule = module {
    viewModel { parameters ->
        HomeViewModel(
            entry = parameters.get(),
            preparation = parameters.getOrNull(),
            router = get(qualifier<MainRouterContext>()),
            observeHomeHistory = get(),
        )
    }
}
