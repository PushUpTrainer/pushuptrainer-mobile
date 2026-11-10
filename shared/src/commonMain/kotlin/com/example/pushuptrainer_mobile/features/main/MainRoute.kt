package com.example.pushuptrainer_mobile.features.main

import androidx.navigation3.runtime.EntryProviderScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.features.main.presentation.MainRoot
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute : Destination

fun Router.navigateToMainScreen(from: NavigationEntry): Boolean =
    returnTo(from, MainRoute)

internal fun EntryProviderScope<Destination>.mainSection() {
    entry<MainRoute> { MainRoot() }
}
