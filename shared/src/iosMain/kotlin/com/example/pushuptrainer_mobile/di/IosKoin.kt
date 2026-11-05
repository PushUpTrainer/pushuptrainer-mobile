package com.example.pushuptrainer_mobile.di

import org.koin.core.context.startKoin
import org.koin.dsl.module

val iosAppModules = module {
    includes(sharedModules)
}

fun initKoinIos() {
    startKoin {
        modules(iosAppModules)
    }
}
