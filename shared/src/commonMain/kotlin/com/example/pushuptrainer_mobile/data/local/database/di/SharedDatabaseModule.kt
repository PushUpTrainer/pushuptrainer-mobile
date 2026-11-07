package com.example.pushuptrainer_mobile.data.local.database.di

import com.example.pushuptrainer_mobile.data.local.database.WorkoutDatabase
import org.koin.dsl.module

val sharedDatabaseModule = module {
    single { get<WorkoutDatabase>().workoutSessionDao() }
}
