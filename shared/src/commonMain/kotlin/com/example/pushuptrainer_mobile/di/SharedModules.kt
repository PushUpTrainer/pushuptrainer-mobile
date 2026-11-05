package com.example.pushuptrainer_mobile.di

import com.example.pushuptrainer_mobile.core.di.sharedCoreModule
import org.koin.dsl.module

val sharedModules = module {
    includes(sharedCoreModule)
}
