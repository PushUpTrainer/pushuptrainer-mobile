package com.example.pushuptrainer_mobile.features.home.di

import com.example.pushuptrainer_mobile.features.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val homeModule = module {
    viewModel { HomeViewModel(observeHomeHistory = get()) }
}
