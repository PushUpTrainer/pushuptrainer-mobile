package com.example.pushuptrainer_mobile.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.koin.compose.getKoin
import org.koin.core.scope.Scope

@OptIn(ExperimentalUuidApi::class)
@Composable
internal fun NavigationRoot(
    startDestination: NavKey,
    savedStateConfiguration: SavedStateConfiguration,
    registerEntries: EntryProviderScope<NavKey>.(Router) -> Unit,
) {
    val koin = getKoin()
    val session = viewModel {
        val scope = koin.createScope<NavigationScope>(Uuid.random().toString())
        NavigationSession(scope.get(), scope.get(), scope)
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val backStack = rememberNavBackStack(savedStateConfiguration, startDestination)
    val entries = remember(session.router, registerEntries) {
        entryProvider { registerEntries(session.router) }
    }

    DisposableEffect(session, backStack, lifecycle) {
        session.controller.attach(backStack) {
            lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        }
        onDispose { session.controller.detach() }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.lastOrNull()?.let(session.router::back) },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entries,
    )
}

private class NavigationSession(
    val router: Router,
    val controller: RouterController,
    private val scope: Scope,
) : ViewModel() {
    override fun onCleared() {
        controller.detach()
        scope.close()
    }
}
