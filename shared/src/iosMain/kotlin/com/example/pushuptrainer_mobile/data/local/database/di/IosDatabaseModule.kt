package com.example.pushuptrainer_mobile.data.local.database.di

import com.example.pushuptrainer_mobile.data.local.database.createWorkoutDatabase
import com.example.pushuptrainer_mobile.data.local.database.workoutDatabaseBuilder
import org.koin.dsl.module

val iosDatabaseModule = module {
    single { createWorkoutDatabase(workoutDatabaseBuilder()) }
}
