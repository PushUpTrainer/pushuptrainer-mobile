package com.example.pushuptrainer_mobile.features.preparation.di

import com.example.pushuptrainer_mobile.features.preparation.presentation.PreparationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.core.qualifier.qualifier
import com.example.pushuptrainer_mobile.features.main.di.MainRouterContext

internal val preparationModule = module {
    viewModel { parameters -> PreparationViewModel(get(), get(qualifier<MainRouterContext>()), parameters.get()) }
}
