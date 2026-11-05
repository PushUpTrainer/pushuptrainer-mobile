package com.example.pushuptrainer_mobile

import androidx.room3.Room
import com.example.pushuptrainer_mobile.data.local.WorkoutDatabase
import com.example.pushuptrainer_mobile.data.local.WorkoutErrorEntity
import com.example.pushuptrainer_mobile.data.local.WorkoutSessionEntity
import com.example.pushuptrainer_mobile.data.local.createWorkoutDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WorkoutStorageIosTest {
    @Test
    fun savesReplacesAndDeletesWorkout() = runBlocking {
        val database = createWorkoutDatabase(Room.inMemoryDatabaseBuilder<WorkoutDatabase>())
        try {
            val dao = database.workoutSessionDao()
            val session = WorkoutSessionEntity("workout-1", 1000, 45000, 8, 10)
            dao.save(session, listOf(WorkoutErrorEntity(session.id, "HANDS_TOO_WIDE", 2)))

            assertEquals(8L, dao.observeTotalRepetitions().first())
            assertEquals(2, dao.getSession(session.id)?.errors?.single()?.repetitions)

            dao.save(session.copy(repetitions = 10), emptyList())
            assertEquals(10L, dao.observeTotalRepetitions().first())
            assertEquals(emptyList(), dao.getSession(session.id)?.errors)

            dao.deleteSession(session.id)
            assertNull(dao.getSession(session.id))
        } finally {
            database.close()
        }
    }
}
