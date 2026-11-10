package com.example.pushuptrainer_mobile.features.home

import androidx.navigation3.runtime.EntryProviderScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.LocalNavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.features.home.presentation.HomeRoot
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : Destination

fun Router.navigateToHomeScreen(from: NavigationEntry): Boolean =
    returnTo(from, HomeRoute)

internal fun EntryProviderScope<Destination>.homeSection() {
    entry<HomeRoute> { HomeRoot(LocalNavigationEntry.current) }
}
