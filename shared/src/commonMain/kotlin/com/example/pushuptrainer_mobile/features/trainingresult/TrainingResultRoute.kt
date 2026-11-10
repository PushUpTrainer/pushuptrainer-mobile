package com.example.pushuptrainer_mobile.features.trainingresult

import androidx.navigation3.runtime.EntryProviderScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.LocalNavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.features.trainingresult.presentation.TrainingResultRoot
import kotlinx.serialization.Serializable

@Serializable
data class TrainingResultRoute(val workoutId: String) : Destination

fun Router.navigateToTrainingResultScreen(from: NavigationEntry, workoutId: String): Boolean =
    open(from, TrainingResultRoute(workoutId))

internal fun EntryProviderScope<Destination>.trainingResultSection(preparation: Destination? = null) {
    entry<TrainingResultRoute> { route ->
        TrainingResultRoot(LocalNavigationEntry.current, route.workoutId, preparation)
    }
}
