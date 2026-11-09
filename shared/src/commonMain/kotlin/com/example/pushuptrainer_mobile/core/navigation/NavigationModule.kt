package com.example.pushuptrainer_mobile.core.navigation

import org.koin.dsl.binds
import org.koin.dsl.module

internal class NavigationScope

internal val navigationModule = module {
    scope<NavigationScope> {
        scoped { StackRouter() } binds arrayOf(Router::class, RouterController::class)
    }
}
