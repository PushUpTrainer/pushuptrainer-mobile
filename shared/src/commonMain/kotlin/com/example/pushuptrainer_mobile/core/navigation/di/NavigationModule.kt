package com.example.pushuptrainer_mobile.core.navigation.di

import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.core.navigation.RouterController
import com.example.pushuptrainer_mobile.core.navigation.StackRouter
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module

internal data object GlobalRouterContext

internal val navigationModule = module {
    single<RouterController>(qualifier<GlobalRouterContext>()) { StackRouter() } bind Router::class
}
