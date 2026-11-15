package com.example.pushuptrainer_mobile.features.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pushuptrainer_mobile.LocalToggleTheme
import com.example.pushuptrainer_mobile.core.navigation.LocalNavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun HomeRoot(mainEntry: NavigationEntry) {
    val entry = LocalNavigationEntry.current
    val toggleTheme by rememberUpdatedState(LocalToggleTheme.current)
    val viewModel = koinViewModel<HomeViewModel> {
        parametersOf(entry, mainEntry)
    }
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                HomeUiEffect.ToggleTheme -> toggleTheme()
            }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(state = state, onEvent = viewModel::onEvent)
}
