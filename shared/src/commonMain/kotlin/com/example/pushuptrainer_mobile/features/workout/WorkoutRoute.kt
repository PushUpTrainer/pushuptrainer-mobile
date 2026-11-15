package com.example.pushuptrainer_mobile.features.workout

import androidx.navigation3.runtime.EntryProviderScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.LocalNavigationEntry
import com.example.pushuptrainer_mobile.features.workout.presentation.WorkoutRoot
import kotlinx.serialization.Serializable

@Serializable
data class WorkoutRoute(val goal: Int?) : Destination {
    init { require(goal == null || goal in 1..100) }
}

internal fun EntryProviderScope<Destination>.workoutSection() {
    entry<WorkoutRoute> { route -> WorkoutRoot(LocalNavigationEntry.current, route.goal) }
}
