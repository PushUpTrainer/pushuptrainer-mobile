package com.example.pushuptrainer_mobile.di

import com.example.pushuptrainer_mobile.core.di.sharedCoreModule
import com.example.pushuptrainer_mobile.data.di.sharedDataModules
import org.koin.dsl.module

val sharedModules = module {
    includes(sharedCoreModule, sharedDataModules)
}
