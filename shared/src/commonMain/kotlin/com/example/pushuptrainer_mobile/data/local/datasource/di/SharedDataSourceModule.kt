package com.example.pushuptrainer_mobile.data.local.datasource.di

import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSource
import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSourceImpl
import org.koin.dsl.module

val sharedDataSourceModule = module {
    single<WorkoutLocalDataSource> { WorkoutLocalDataSourceImpl(get()) }
}
