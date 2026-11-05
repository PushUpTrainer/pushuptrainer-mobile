package com.example.pushuptrainer_mobile.di

import org.koin.dsl.module

val androidAppModules = module {
    includes(sharedModules)
}
