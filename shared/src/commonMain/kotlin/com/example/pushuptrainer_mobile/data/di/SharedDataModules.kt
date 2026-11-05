package com.example.pushuptrainer_mobile.data.di

import com.example.pushuptrainer_mobile.data.local.di.sharedLocalModule
import org.koin.dsl.module

val sharedDataModules = module {
    includes(sharedLocalModule)
}
