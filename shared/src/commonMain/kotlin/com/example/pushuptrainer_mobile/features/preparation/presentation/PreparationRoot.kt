package com.example.pushuptrainer_mobile.features.preparation.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.example.pushuptrainer_mobile.LocalToggleTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PreparationRoot(viewModel: PreparationViewModel = koinViewModel()) {
    val toggleTheme by rememberUpdatedState(LocalToggleTheme.current)
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                PreparationUiEffect.ToggleTheme -> toggleTheme()
            }
        }
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = state.isHowToPlacePhoneVisible,
        onBackCompleted = { viewModel.onEvent(PreparationUiEvent.HowToPlacePhoneDismissed) },
    )
    PreparationScreen(state = state, onEvent = viewModel::onEvent)
}
