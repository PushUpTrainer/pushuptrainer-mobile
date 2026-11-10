package com.example.pushuptrainer_mobile.features.main.di

import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.core.navigation.RouterController
import com.example.pushuptrainer_mobile.core.navigation.StackRouter
import com.example.pushuptrainer_mobile.features.main.presentation.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module

internal data object MainRouterContext

internal val mainModule = module {
    single<RouterController>(qualifier<MainRouterContext>()) { StackRouter() } bind Router::class
    viewModel { MainViewModel(get(qualifier<MainRouterContext>())) }
}
