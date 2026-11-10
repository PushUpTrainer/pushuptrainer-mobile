package com.example.pushuptrainer_mobile.di

import com.example.pushuptrainer_mobile.data.local.di.iosLocalModule
import org.koin.core.context.startKoin
import org.koin.dsl.module

val iosAppModules = module {
    includes(sharedModules, iosLocalModule)
}

fun initKoinIos() {
    startKoin {
        modules(iosAppModules)
    }
}
