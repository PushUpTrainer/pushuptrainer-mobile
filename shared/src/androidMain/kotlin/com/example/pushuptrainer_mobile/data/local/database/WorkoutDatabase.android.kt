package com.example.pushuptrainer_mobile.data.local.database

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase

fun workoutDatabaseBuilder(context: Context): RoomDatabase.Builder<WorkoutDatabase> {
    val appContext = context.applicationContext
    return Room.databaseBuilder<WorkoutDatabase>(
        context = appContext,
        name = appContext.getDatabasePath("pushuptrainer.db").absolutePath,
    )
}
