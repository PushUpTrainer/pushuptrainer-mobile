package com.example.pushuptrainer_mobile.features.preparation.di

import com.example.pushuptrainer_mobile.features.preparation.presentation.PreparationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val preparationModule = module {
    viewModel { PreparationViewModel() }
}
