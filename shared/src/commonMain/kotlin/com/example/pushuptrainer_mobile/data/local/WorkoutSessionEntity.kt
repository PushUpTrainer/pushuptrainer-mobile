package com.example.pushuptrainer_mobile.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val startedAtEpochMs: Long,
    val durationMs: Long,
    val repetitions: Int,
    val goalRepetitions: Int?,
)
