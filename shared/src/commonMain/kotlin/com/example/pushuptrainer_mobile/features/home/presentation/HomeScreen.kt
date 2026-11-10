package com.example.pushuptrainer_mobile.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pushuptrainer_mobile.LocalToggleTheme
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import com.example.pushuptrainer_mobile.core.uikit.components.primaryButton.AppPrimaryButton
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeader
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeaderUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCard
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryCard
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarCard
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.main_prepare
import pushuptrainermobile.shared.generated.resources.main_subtitle
import pushuptrainermobile.shared.generated.resources.main_title
import pushuptrainermobile.shared.generated.resources.main_history_unavailable

@Composable
internal fun HomeRoot(
    entry: NavigationEntry,
    preparation: Destination? = null,
    viewModel: HomeViewModel = koinViewModel(parameters = { parametersOf(entry, preparation) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onEvent = { viewModel.onEvent(it) },
        onThemeToggle = LocalToggleTheme.current,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    onThemeToggle: () -> Unit = {},
) {
    Column(modifier = modifier.fillMaxSize().background(AppTheme.colors.background)) {
        HomeHeader(onThemeToggle, Modifier.padding(horizontal = 20.dp))
        when (state) {
            is HomeUiState.Loading -> Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }

            is HomeUiState.Unavailable -> Box(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.main_history_unavailable),
                    color = AppTheme.colors.textSecondary,
                )
            }

            is HomeUiState.Content -> HomeContent(state, Modifier.weight(1f))
        }
        AppPrimaryButton(
            text = stringResource(Res.string.main_prepare),
            enabled = state.canPrepare,
            onClick = { onEvent(HomeUiEvent.PrepareClicked) },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HomeContent(state: HomeUiState.Content, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        state.streak?.let { streak ->
            StreakCard(model = streak, isHighlighted = state.today is TodaySummaryUiModel.Done)
            Spacer(Modifier.height(16.dp))
        }
        WeekCalendarCard(model = state.week)
        Spacer(Modifier.height(16.dp))
        TodaySummaryCard(model = state.today, showPreparationHint = state.canPrepare)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HomeHeader(onThemeToggle: () -> Unit, modifier: Modifier = Modifier) {
    ScreenHeader(
        model = ScreenHeaderUiModel(
            title = stringResource(Res.string.main_title),
            subtitle = stringResource(Res.string.main_subtitle),
        ),
        onThemeToggleClick = onThemeToggle,
        modifier = modifier,
    )
}

// region Preview

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeBeforeWorkoutDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        HomeScreen(state = HomePreviewData.beforeWorkout, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeBeforeWorkoutLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        HomeScreen(state = HomePreviewData.beforeWorkout, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeAfterWorkoutDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        HomeScreen(state = HomePreviewData.afterWorkout, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeAfterWorkoutLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        HomeScreen(state = HomePreviewData.afterWorkout, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun HomeLoadingDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        HomeScreen(state = HomeUiState.Loading(canPrepare = false), onEvent = {})
    }
}

// endregion
