package com.example.pushuptrainer_mobile.data.local.di

import com.example.pushuptrainer_mobile.data.local.database.di.sharedDatabaseModule
import com.example.pushuptrainer_mobile.data.local.datasource.di.sharedDataSourceModule
import org.koin.dsl.module

val sharedLocalModule = module {
    includes(sharedDatabaseModule, sharedDataSourceModule)
}
