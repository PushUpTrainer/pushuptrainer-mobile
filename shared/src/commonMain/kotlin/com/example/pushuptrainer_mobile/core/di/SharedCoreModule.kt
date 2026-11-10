package com.example.pushuptrainer_mobile.core.di

import com.example.pushuptrainer_mobile.Greeting
import com.example.pushuptrainer_mobile.core.navigation.di.navigationModule
import org.koin.dsl.module

private val greetingModule = module {
    single { Greeting() }
}

val sharedCoreModule = module {
    includes(greetingModule, navigationModule)
}
