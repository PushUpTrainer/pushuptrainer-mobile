package com.example.pushuptrainer_mobile.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutSessionEntity
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutErrorEntity
import com.example.pushuptrainer_mobile.data.local.database.entity.WorkoutSessionWithErrors
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WorkoutSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertSession(session: WorkoutSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertErrors(errors: List<WorkoutErrorEntity>)

    @Query("DELETE FROM workout_errors WHERE sessionId = :sessionId")
    protected abstract suspend fun deleteErrors(sessionId: String)

    @Transaction
    open suspend fun save(session: WorkoutSessionEntity, errors: List<WorkoutErrorEntity>) {
        require(session.id.isNotBlank())
        require(session.startedAtEpochMs >= 0)
        require(session.durationMs >= 0)
        require(session.repetitions >= 0)
        require(session.goalRepetitions == null || session.goalRepetitions > 0)
        require(errors.all { it.sessionId == session.id && it.code.isNotBlank() && it.repetitions > 0 })
        require(errors.map { it.code }.distinct().size == errors.size)
        upsertSession(session)
        deleteErrors(session.id)
        if (errors.isNotEmpty()) insertErrors(errors)
    }

    @Transaction
    @Query("SELECT * FROM workout_sessions ORDER BY startedAtEpochMs DESC, id DESC")
    abstract fun observeSessions(): Flow<List<WorkoutSessionWithErrors>>

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    abstract suspend fun getSession(id: String): WorkoutSessionWithErrors?

    @Query("SELECT COALESCE(SUM(repetitions), 0) FROM workout_sessions")
    abstract fun observeTotalRepetitions(): Flow<Long>

    @Query("DELETE FROM workout_sessions WHERE id = :id")
    abstract suspend fun deleteSession(id: String)
}
