package com.example.pushuptrainer_mobile.core.di

import com.example.pushuptrainer_mobile.Greeting
import org.koin.dsl.module

val sharedCoreModule = module {
    single { Greeting() }
}
