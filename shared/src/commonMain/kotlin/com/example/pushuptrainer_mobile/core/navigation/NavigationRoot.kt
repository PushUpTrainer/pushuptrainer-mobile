package com.example.pushuptrainer_mobile.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration

internal val LocalNavigationEntry = staticCompositionLocalOf<NavigationEntry> {
    error("Navigation entry is unavailable")
}

@Composable
internal fun NavigationRoot(
    controller: RouterController,
    startDestination: Destination,
    savedStateConfiguration: SavedStateConfiguration,
    registerEntries: EntryProviderScope<Destination>.() -> Unit,
    isActive: () -> Boolean = { true },
    content: @Composable (NavigationEntry, @Composable (Modifier) -> Unit) -> Unit =
        { _, display -> display(Modifier.fillMaxSize()) },
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val active = rememberUpdatedState(isActive)
    val firstEntry = remember(startDestination) { startDestination.newEntry() }
    val backStack = rememberNavBackStack(savedStateConfiguration, firstEntry)
    val entries = remember(registerEntries) { entryProvider<Destination> { registerEntries() } }

    DisposableEffect(controller, backStack, lifecycle) {
        val detach = controller.attach(backStack) {
            lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && active.value()
        }
        onDispose(detach)
    }

    content(backStack.last() as NavigationEntry) { modifier ->
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = { controller.back(backStack.last() as NavigationEntry) },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = { key: NavKey ->
                val occurrence = key as NavigationEntry
                val entry = entries(occurrence.destination)
                NavEntry(key, contentKey = occurrence.id, metadata = entry.metadata) {
                    CompositionLocalProvider(LocalNavigationEntry provides occurrence) {
                        entry.Content()
                    }
                }
            },
        )
    }
}
