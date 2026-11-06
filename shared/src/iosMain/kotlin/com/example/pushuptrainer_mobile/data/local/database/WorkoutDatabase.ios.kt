package com.example.pushuptrainer_mobile.data.local.database

import androidx.room3.Room
import androidx.room3.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
fun workoutDatabaseBuilder(): RoomDatabase.Builder<WorkoutDatabase> {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )?.path ?: error("Application Support directory is unavailable")
    return Room.databaseBuilder<WorkoutDatabase>(name = "$directory/pushuptrainer.db")
}
