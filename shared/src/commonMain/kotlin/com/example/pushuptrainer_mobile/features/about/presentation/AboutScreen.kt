package com.example.pushuptrainer_mobile.features.about.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationBar
import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination
import com.example.pushuptrainer_mobile.core.uikit.components.repositoryLink.RepositoryLink
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeader
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeaderUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.teamCard.TeamCard
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.about_subtitle
import pushuptrainermobile.shared.generated.resources.about_title

/** Точка входа экрана для продакшена: берёт состояние из ViewModel. */
@Composable
fun AboutRoute(
    viewModel: AboutViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AboutScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

/** Экран «О проекте» (макет 3.0, светлая и тёмная темы). */
@Composable
fun AboutScreen(
    state: AboutUiState,
    onEvent: (AboutUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .safeDrawingPadding(),
    ) {
        when (state) {
            AboutUiState.Loading -> AboutLoading(
                onEvent = onEvent,
                modifier = Modifier.weight(1f),
            )

            is AboutUiState.Content -> AboutContent(
                state = state,
                onEvent = onEvent,
                modifier = Modifier.weight(1f),
            )
        }
        BottomNavigationBar(
            selected = BottomNavigationDestination.About,
            onDestinationClick = { onEvent(AboutUiEvent.BottomBarDestinationClicked(it)) },
        )
    }
}

@Composable
private fun AboutContent(
    state: AboutUiState.Content,
    onEvent: (AboutUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        AboutHeader(onEvent = onEvent)
        Spacer(Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            state.teams.forEach { team ->
                TeamCard(model = team)
            }
            RepositoryLink(
                url = state.repositoryUrl,
                onClick = { onEvent(AboutUiEvent.RepositoryLinkClicked) },
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun AboutLoading(
    onEvent: (AboutUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        AboutHeader(onEvent = onEvent)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = AppTheme.colors.accent)
        }
    }
}

@Composable
private fun AboutHeader(onEvent: (AboutUiEvent) -> Unit) {
    ScreenHeader(
        model = ScreenHeaderUiModel(
            title = stringResource(Res.string.about_title),
            subtitle = stringResource(Res.string.about_subtitle),
        ),
        onThemeToggleClick = { onEvent(AboutUiEvent.ThemeToggleClicked) },
    )
}

// region Preview

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun AboutLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        AboutScreen(state = AboutPreviewData.content, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun AboutDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        AboutScreen(state = AboutPreviewData.content, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun AboutLoadingPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        AboutScreen(state = AboutUiState.Loading, onEvent = {})
    }
}

// endregion
