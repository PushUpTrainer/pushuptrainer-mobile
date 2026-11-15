package com.example.pushuptrainer_mobile.features.workout

import androidx.room3.Room
import com.example.pushuptrainer_mobile.data.local.database.WorkoutDatabase
import com.example.pushuptrainer_mobile.data.local.database.createWorkoutDatabase
import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSource
import com.example.pushuptrainer_mobile.data.local.di.androidLocalModule
import com.example.pushuptrainer_mobile.di.sharedModules
import com.example.pushuptrainer_mobile.features.trainingresult.domain.usecase.GetTrainingResultUseCase
import com.example.pushuptrainer_mobile.features.workout.domain.model.CompletedWorkout
import com.example.pushuptrainer_mobile.features.workout.domain.usecase.SaveWorkoutUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import androidx.test.platform.app.InstrumentationRegistry

class WorkoutStorageAndroidTest {
    @Test
    fun runtimeSaveIsIdempotentAndResultReadsRealRoomData() = runBlocking {
        val database = createWorkoutDatabase(Room.inMemoryDatabaseBuilder<WorkoutDatabase>(InstrumentationRegistry.getInstrumentation().targetContext))
        val application = koinApplication {
            modules(sharedModules, androidLocalModule, module { single<WorkoutDatabase> { database } })
        }
        try {
            val save = application.koin.get<SaveWorkoutUseCase>()
            val source = application.koin.get<WorkoutLocalDataSource>()
            val getResult = application.koin.get<GetTrainingResultUseCase>()
            val workout = CompletedWorkout("runtime-android", 1000, 45000, 8, null, mapOf("HANDS_TOO_WIDE" to 2))
            save(workout)
            save(workout)
            assertEquals(1, source.observeSessions().first().size)
            assertEquals(8L, source.observeTotalRepetitions().first())
            assertEquals(45000L, source.getSession(workout.id)?.session?.durationMs)
            assertEquals(2, source.getSession(workout.id)?.errors?.single()?.repetitions)
            assertEquals(8, getResult(workout.id)?.repetitions)
            assertEquals(null, getResult(workout.id)?.goal)
        } finally {
            application.close()
            database.close()
        }
    }
}
