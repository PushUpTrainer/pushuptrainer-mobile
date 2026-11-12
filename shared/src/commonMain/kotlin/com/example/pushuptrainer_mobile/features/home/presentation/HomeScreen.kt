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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.components.onboardingCard.OnboardingCard
import com.example.pushuptrainer_mobile.core.uikit.components.primaryButton.AppPrimaryButton
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeader
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeaderUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCard
import com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard.TodaySummaryCard
import com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard.WeekCalendarCard
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.main_history_unavailable
import pushuptrainermobile.shared.generated.resources.main_prepare
import pushuptrainermobile.shared.generated.resources.main_retry
import pushuptrainermobile.shared.generated.resources.main_subtitle
import pushuptrainermobile.shared.generated.resources.main_title

@Composable
fun HomeScreen(
    state: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(AppTheme.colors.background)) {
        HomeHeader({ onEvent(HomeUiEvent.ThemeToggleClicked) }, Modifier.padding(horizontal = 20.dp))
        when (state) {
            HomeUiState.Loading -> Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }

            HomeUiState.Unavailable -> Box(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center,
            ) {
                HistoryError(onRetry = { onEvent(HomeUiEvent.RetryClicked) })
            }

            is HomeUiState.Content -> HomeContent(state, onEvent, Modifier.weight(1f))
        }
        AppPrimaryButton(
            text = stringResource(Res.string.main_prepare),
            onClick = { onEvent(HomeUiEvent.PrepareClicked) },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HomeContent(state: HomeUiState.Content, onEvent: (HomeUiEvent) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        StreakCard(model = state.streak, isHighlighted = state.hasWorkoutToday)
        Spacer(Modifier.height(16.dp))
        WeekCalendarCard(
            model = state.week,
            onDayClick = { onEvent(HomeUiEvent.DaySelected(it)) },
            onPreviousWeekClick = { onEvent(HomeUiEvent.PreviousWeekClicked) },
            onNextWeekClick = { onEvent(HomeUiEvent.NextWeekClicked) },
            onTodayClick = { onEvent(HomeUiEvent.TodayClicked) },
        )
        Spacer(Modifier.height(16.dp))
        OnboardingCard(onClick = { onEvent(HomeUiEvent.OnboardingClicked) })
        Spacer(Modifier.height(16.dp))
        if (state.historyUnavailable) {
            HistoryError(onRetry = { onEvent(HomeUiEvent.RetryClicked) })
        }
        TodaySummaryCard(model = state.today)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun HistoryError(onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(Res.string.main_history_unavailable), color = AppTheme.colors.textSecondary)
        TextButton(onClick = onRetry) {
            Text(stringResource(Res.string.main_retry), color = AppTheme.colors.accent)
        }
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
        HomeScreen(state = HomeUiState.Loading, onEvent = {})
    }
}

// endregion
