package com.example.pushuptrainer_mobile.data.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

@Database(
    entities = [WorkoutSessionEntity::class, WorkoutErrorEntity::class],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(WorkoutDatabaseConstructor::class)
abstract class WorkoutDatabase : RoomDatabase() {
    abstract fun workoutSessionDao(): WorkoutSessionDao
}

@Suppress("KotlinNoActualForExpect")
expect object WorkoutDatabaseConstructor : RoomDatabaseConstructor<WorkoutDatabase> {
    override fun initialize(): WorkoutDatabase
}

fun createWorkoutDatabase(builder: RoomDatabase.Builder<WorkoutDatabase>): WorkoutDatabase =
    builder.setDriver(BundledSQLiteDriver()).build()
