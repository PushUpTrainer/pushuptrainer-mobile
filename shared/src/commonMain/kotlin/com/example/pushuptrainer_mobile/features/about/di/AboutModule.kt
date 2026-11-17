package com.example.pushuptrainer_mobile.features.about.di

import com.example.pushuptrainer_mobile.features.about.presentation.AboutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val aboutModule = module {
    viewModel { AboutViewModel() }
}
