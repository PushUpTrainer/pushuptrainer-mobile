package com.example.pushuptrainer_mobile.features.preparation

import androidx.navigation3.runtime.EntryProviderScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.features.preparation.presentation.PreparationRoot
import kotlinx.serialization.Serializable

@Serializable
data object PreparationRoute : Destination

fun Router.navigateToPreparationScreen(from: NavigationEntry, replaceCurrent: Boolean = false): Boolean =
    if (replaceCurrent) replace(from, PreparationRoute) else open(from, PreparationRoute)

internal fun EntryProviderScope<Destination>.preparationSection() {
    entry<PreparationRoute> { PreparationRoot() }
}
