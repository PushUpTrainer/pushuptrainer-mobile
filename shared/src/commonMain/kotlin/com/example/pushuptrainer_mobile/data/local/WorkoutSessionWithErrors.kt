package com.example.pushuptrainer_mobile.data.local

import androidx.room3.Embedded
import androidx.room3.Relation

data class WorkoutSessionWithErrors(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["sessionId"],
    )
    val errors: List<WorkoutErrorEntity>,
)
