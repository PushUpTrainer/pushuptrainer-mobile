package com.example.pushuptrainer_mobile.data.local.database.di

import com.example.pushuptrainer_mobile.data.local.database.createWorkoutDatabase
import com.example.pushuptrainer_mobile.data.local.database.workoutDatabaseBuilder
import org.koin.dsl.module

val androidDatabaseModule = module {
    single { createWorkoutDatabase(workoutDatabaseBuilder(get())) }
}
