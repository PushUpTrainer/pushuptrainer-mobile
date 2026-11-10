package com.example.pushuptrainer_mobile.data.local.datasource

import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutErrorEntity
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutSessionEntity
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutSessionWithErrors
import kotlinx.coroutines.flow.Flow

interface WorkoutLocalDataSource {
    suspend fun save(session: WorkoutSessionEntity, errors: List<WorkoutErrorEntity>)
    fun observeSessions(): Flow<List<WorkoutSessionWithErrors>>
    suspend fun getSession(id: String): WorkoutSessionWithErrors?
    fun observeTotalRepetitions(): Flow<Long>
    suspend fun deleteSession(id: String)
}
