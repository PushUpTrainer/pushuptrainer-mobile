package com.example.pushuptrainer_mobile.features.main.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationBar
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.features.main.MainNavGraph
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MainRoot(viewModel: MainViewModel = koinViewModel()) {
    MainNavGraph { entry, display ->
        MainScreen(
            state = viewModel.state(entry),
            onEvent = { viewModel.onEvent(entry, it) },
            content = display,
        )
    }
}

@Composable
private fun MainScreen(
    state: MainUiState,
    onEvent: (MainUiEvent) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            BottomNavigationBar(
                selected = state.selected,
                enabledDestinations = state.available,
                onDestinationClick = { onEvent(MainUiEvent.TabSelected(it)) },
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
                ),
            )
        },
    ) { padding ->
        content(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding))
    }
}
