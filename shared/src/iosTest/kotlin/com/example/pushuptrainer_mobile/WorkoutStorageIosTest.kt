package com.example.pushuptrainer_mobile

import androidx.room3.Room
import com.example.pushuptrainer_mobile.data.local.database.WorkoutDatabase
import com.example.pushuptrainer_mobile.data.local.database.createWorkoutDatabase
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutErrorEntity
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutSessionEntity
import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSource
import com.example.pushuptrainer_mobile.di.iosAppModules
import com.example.pushuptrainer_mobile.features.home.domain.usecase.ObserveHomeHistoryUseCase
import com.example.pushuptrainer_mobile.features.trainingresult.domain.usecase.GetTrainingResultUseCase
import com.example.pushuptrainer_mobile.features.workout.domain.model.CompletedWorkout
import com.example.pushuptrainer_mobile.features.workout.domain.usecase.SaveWorkoutUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class WorkoutStorageIosTest {
    @Test
    fun zeroWorkoutsAreNotSavedAndStatisticsObserveRealRoomChanges() = runBlocking {
        val database = createWorkoutDatabase(Room.inMemoryDatabaseBuilder<WorkoutDatabase>())
        val application = koinApplication { modules(iosAppModules, module { single<WorkoutDatabase> { database } }) }
        try {
            val save = application.koin.get<SaveWorkoutUseCase>()
            val source = application.koin.get<WorkoutLocalDataSource>()
            val progress = application.koin.get<com.example.pushuptrainer_mobile.features.progress.domain.usecase.ObserveProgressUseCase>()
            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
            val zero = CompletedWorkout("zero", now, 5000, 0, null, emptyMap())
            assertEquals(false, save(zero))
            assertEquals(null, source.getSession("zero"))
            assertEquals(0, progress(null).first().dayWorkouts)
            save(zero.copy(id = "positive", repetitions = 7))
            source.save(WorkoutSessionEntity("legacy-zero", now, 1000, 0, null), emptyList())
            val history = progress(null).first()
            assertEquals(1, history.dayWorkouts)
            assertEquals(7, history.dayRepetitions)
            assertEquals(7L, history.totalRepetitions)
            assertEquals(7, history.bestWorkout)
            val home = application.koin.get<ObserveHomeHistoryUseCase>()().first()
            assertEquals(1, home.workouts)
            assertEquals(7, home.repetitions)
            source.deleteSession("positive")
            assertEquals(0, progress(null).first().dayWorkouts)
        } finally {
            application.close()
            database.close()
        }
    }

    @Test
    fun workoutSaveUseCasePersistsOneResultAndPreservesFirstSnapshotOnRetry() = runBlocking {
        val database = createWorkoutDatabase(Room.inMemoryDatabaseBuilder<WorkoutDatabase>())
        val application = koinApplication {
            modules(iosAppModules, module { single<WorkoutDatabase> { database } })
        }
        try {
            val save = application.koin.get<SaveWorkoutUseCase>()
            val source = application.koin.get<WorkoutLocalDataSource>()
            val getResult = application.koin.get<GetTrainingResultUseCase>()
            val workout = CompletedWorkout("runtime-one", 1000, 42000, 5, 5, mapOf("HANDS_TOO_WIDE" to 2))
            save(workout)
            save(workout.copy(repetitions = 6))
            assertEquals(1, source.observeSessions().first().size)
            assertEquals(5L, source.observeTotalRepetitions().first())
            assertEquals(42000L, source.getSession(workout.id)?.session?.durationMs)
            assertEquals(2, source.getSession(workout.id)?.errors?.single()?.repetitions)
            assertEquals(5, getResult(workout.id)?.repetitions)
            assertEquals(5, getResult(workout.id)?.goal)
        } finally {
            application.close()
            database.close()
        }
    }

    @Test
    fun savesReplacesAndDeletesWorkout() = runBlocking {
        val database = createWorkoutDatabase(Room.inMemoryDatabaseBuilder<WorkoutDatabase>())
        val application = koinApplication {
            modules(iosAppModules, module { single<WorkoutDatabase> { database } })
        }
        try {
            val dataSource = application.koin.get<WorkoutLocalDataSource>()
            val observeHomeHistory = application.koin.get<ObserveHomeHistoryUseCase>()
            val getTrainingResult = application.koin.get<GetTrainingResultUseCase>()
            val session = WorkoutSessionEntity("workout-1", 1000, 45000, 8, 10)
            val history = observeHomeHistory().produceIn(this)
            val home = try {
                assertTrue(withTimeout(5_000) { history.receive() }.workoutDays.isEmpty())
                dataSource.save(session, listOf(WorkoutErrorEntity(session.id, "HANDS_TOO_WIDE", 2)))
                withTimeout(5_000) { history.receive() }
            } finally {
                history.cancel()
            }

            assertEquals(8L, dataSource.observeTotalRepetitions().first())
            assertEquals(session, dataSource.observeSessions().first().single().session)
            assertEquals(2, dataSource.getSession(session.id)?.errors?.single()?.repetitions)
            val workoutDate = Instant.fromEpochMilliseconds(session.startedAtEpochMs)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
            assertEquals(setOf(workoutDate), home.workoutDays)
            assertEquals(0, home.workouts)
            val result = getTrainingResult(session.id)
            assertEquals(8, result?.repetitions)
            assertEquals(10, result?.goal)

            dataSource.save(session.copy(repetitions = 10), emptyList())
            assertEquals(10L, dataSource.observeTotalRepetitions().first())
            assertEquals(emptyList(), dataSource.getSession(session.id)?.errors)

            dataSource.deleteSession(session.id)
            assertNull(dataSource.getSession(session.id))
            assertNull(getTrainingResult(session.id))
            assertEquals(emptyList(), dataSource.observeSessions().first())
            assertEquals(0L, dataSource.observeTotalRepetitions().first())
        } finally {
            application.close()
            database.close()
        }
    }
}
