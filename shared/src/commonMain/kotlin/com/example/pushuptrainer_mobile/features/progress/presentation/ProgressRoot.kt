package com.example.pushuptrainer_mobile.features.progress.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pushuptrainer_mobile.LocalToggleTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProgressRoot(viewModel: ProgressViewModel = koinViewModel()) {
    val toggleTheme by rememberUpdatedState(LocalToggleTheme.current)
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { if (it == ProgressUiEffect.ToggleTheme) toggleTheme() }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProgressScreen(state, viewModel::onEvent)
}
