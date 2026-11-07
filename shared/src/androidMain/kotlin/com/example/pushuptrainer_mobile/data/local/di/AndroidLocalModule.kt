package com.example.pushuptrainer_mobile.data.local.di

import com.example.pushuptrainer_mobile.data.local.database.di.androidDatabaseModule
import org.koin.dsl.module

val androidLocalModule = module {
    includes(androidDatabaseModule)
}
