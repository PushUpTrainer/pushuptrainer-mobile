package com.example.pushuptrainer_mobile.features.workout.domain.model

import com.example.pushuptrainer_mobile.ml.domain.model.PushupState

internal enum class WorkoutPhase { Starting, Running, Paused, CameraError, Finished }

internal data class WorkoutSessionState(
    val phase: WorkoutPhase = WorkoutPhase.Starting,
    val generation: Int = 0,
    val analysis: PushupState? = null,
)
