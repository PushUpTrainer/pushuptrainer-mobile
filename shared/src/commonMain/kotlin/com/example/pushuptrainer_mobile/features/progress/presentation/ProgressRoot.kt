package com.example.pushuptrainer_mobile.features.progress.presentation

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
internal fun ProgressRoot(
    entry: NavigationEntry = LocalNavigationEntry.current,
    viewModel: ProgressViewModel = koinViewModel(parameters = { parametersOf(entry) }),
) {
    val toggleTheme by rememberUpdatedState(LocalToggleTheme.current)
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { if (it == ProgressUiEffect.ToggleTheme) toggleTheme() }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProgressScreen(state, viewModel::onEvent)
}
