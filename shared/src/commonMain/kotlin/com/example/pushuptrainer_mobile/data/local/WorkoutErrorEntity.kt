package com.example.pushuptrainer_mobile.data.local

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index

@Entity(
    tableName = "workout_errors",
    primaryKeys = ["sessionId", "code"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class WorkoutErrorEntity(
    val sessionId: String,
    val code: String,
    val repetitions: Int,
)
