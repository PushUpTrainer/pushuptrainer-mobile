package com.example.pushuptrainer_mobile.features.progress.di

import com.example.pushuptrainer_mobile.features.main.di.MainRouterContext
import com.example.pushuptrainer_mobile.features.progress.data.repository.di.progressRepositoryModule
import com.example.pushuptrainer_mobile.features.progress.domain.usecase.di.progressDomainModule
import com.example.pushuptrainer_mobile.features.progress.presentation.ProgressViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

internal val progressModule = module {
    includes(progressRepositoryModule, progressDomainModule)
    viewModel { parameters ->
        ProgressViewModel(get(), get(), parameters.get(), get(qualifier<MainRouterContext>()))
    }
}
