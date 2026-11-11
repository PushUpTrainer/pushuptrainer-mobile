package com.example.pushuptrainer_mobile.features.home

import androidx.navigation3.runtime.EntryProviderScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.LocalNavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.features.home.presentation.HomeRoot
import com.example.pushuptrainer_mobile.features.main.di.MainRouterContext
import com.example.pushuptrainer_mobile.features.preparation.navigateToPreparationScreen
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.core.qualifier.qualifier

@Serializable
data object HomeRoute : Destination

fun Router.navigateToHomeScreen(from: NavigationEntry): Boolean =
    returnTo(from, HomeRoute)

internal fun EntryProviderScope<Destination>.homeSection() {
    entry<HomeRoute> {
        val entry = LocalNavigationEntry.current
        val router = koinInject<Router>(qualifier<MainRouterContext>())
        HomeRoot(onPrepare = { router.navigateToPreparationScreen(entry) })
    }
}
