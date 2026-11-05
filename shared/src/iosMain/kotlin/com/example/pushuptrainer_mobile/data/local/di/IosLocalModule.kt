package com.example.pushuptrainer_mobile.data.local.di

import com.example.pushuptrainer_mobile.data.local.createWorkoutDatabase
import com.example.pushuptrainer_mobile.data.local.workoutDatabaseBuilder
import org.koin.dsl.module

val iosLocalModule = module {
    single { createWorkoutDatabase(workoutDatabaseBuilder()) }
}
