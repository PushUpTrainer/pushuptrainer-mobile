package com.example.pushuptrainer_mobile.data.local.datasource

import com.example.pushuptrainer_mobile.data.local.database.dao.WorkoutSessionDao
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutErrorEntity
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutSessionEntity
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutSessionWithErrors
import kotlinx.coroutines.flow.Flow

class WorkoutLocalDataSourceImpl(
    private val workoutSessionDao: WorkoutSessionDao,
) : WorkoutLocalDataSource {
    override suspend fun save(session: WorkoutSessionEntity, errors: List<WorkoutErrorEntity>) =
        workoutSessionDao.save(session, errors)

    override fun observeSessions(): Flow<List<WorkoutSessionWithErrors>> =
        workoutSessionDao.observeSessions()

    override suspend fun getSession(id: String): WorkoutSessionWithErrors? =
        workoutSessionDao.getSession(id)

    override fun observeTotalRepetitions(): Flow<Long> =
        workoutSessionDao.observeTotalRepetitions()

    override suspend fun deleteSession(id: String) =
        workoutSessionDao.deleteSession(id)
}
