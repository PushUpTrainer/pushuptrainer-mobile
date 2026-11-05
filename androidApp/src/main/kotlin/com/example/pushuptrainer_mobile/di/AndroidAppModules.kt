package com.example.pushuptrainer_mobile.di

import com.example.pushuptrainer_mobile.data.local.di.androidLocalModule
import org.koin.dsl.module

val androidAppModules = module {
    includes(sharedModules, androidLocalModule)
}
