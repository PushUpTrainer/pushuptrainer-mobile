package com.example.pushuptrainer_mobile.ml.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.pushuptrainer_mobile.ml.domain.model.PoseListener

interface WorkoutCameraListener : PoseListener {
    fun onReady()
    fun onError()
}

val LocalWorkoutCamera = staticCompositionLocalOf<@Composable (WorkoutCameraListener) -> Unit> {
    { error("Workout camera is not installed") }
}
