package com.example.pushuptrainer_mobile.data.local.di

import com.example.pushuptrainer_mobile.data.local.WorkoutDatabase
import org.koin.dsl.module

val sharedLocalModule = module {
    single { get<WorkoutDatabase>().workoutSessionDao() }
}
