package com.example.pushuptrainer_mobile.features.main.presentation

import androidx.compose.foundation.background
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
import com.example.pushuptrainer_mobile.core.uikit.components.hintCard.HintCard
import com.example.pushuptrainer_mobile.core.uikit.components.hintCard.HintCardUiModel
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
import pushuptrainermobile.shared.generated.resources.main_how_it_works_subtitle
import pushuptrainermobile.shared.generated.resources.main_how_it_works_title
import pushuptrainermobile.shared.generated.resources.main_prepare
import pushuptrainermobile.shared.generated.resources.main_subtitle
import pushuptrainermobile.shared.generated.resources.main_title

/** Точка входа экрана для продакшена: берёт состояние из ViewModel. */
@Composable
fun MainRoute(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MainScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

/** Главный экран (макеты 1.0.0 «до тренировки» и 1.0.1 «после тренировки»). */
@Composable
fun MainScreen(
    state: MainUiState,
    onEvent: (MainUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .safeDrawingPadding(),
    ) {
        when (state) {
            MainUiState.Loading -> MainLoading(
                onEvent = onEvent,
                modifier = Modifier.weight(1f),
            )

            is MainUiState.Content -> MainContent(
                state = state,
                onEvent = onEvent,
                modifier = Modifier.weight(1f),
            )
        }
        BottomNavigationBar(
            selected = BottomNavigationDestination.Trainer,
            onDestinationClick = { onEvent(MainUiEvent.BottomBarDestinationClicked(it)) },
        )
    }
}

@Composable
private fun MainContent(
    state: MainUiState.Content,
    onEvent: (MainUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Карточки прокручиваются на маленьких экранах, а кнопка всегда остаётся внизу.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            MainHeader(onEvent = onEvent)
            Spacer(Modifier.height(20.dp))
            StreakCard(
                model = state.streak,
                isHighlighted = state.isStreakHighlighted,
            )
            Spacer(Modifier.height(16.dp))
            WeekCalendarCard(model = state.week)
            Spacer(Modifier.height(16.dp))
            HintCard(
                model = HintCardUiModel(
                    title = stringResource(Res.string.main_how_it_works_title),
                    subtitle = stringResource(Res.string.main_how_it_works_subtitle),
                ),
                onClick = { onEvent(MainUiEvent.HowItWorksClicked) },
            )
            Spacer(Modifier.height(16.dp))
            TodaySummaryCard(model = state.today)
            Spacer(Modifier.height(16.dp))
        }
        AppPrimaryButton(
            text = stringResource(Res.string.main_prepare),
            onClick = { onEvent(MainUiEvent.PrepareClicked) },
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun MainLoading(
    onEvent: (MainUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        MainHeader(onEvent = onEvent)
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
private fun MainHeader(onEvent: (MainUiEvent) -> Unit) {
    ScreenHeader(
        model = ScreenHeaderUiModel(
            title = stringResource(Res.string.main_title),
            subtitle = stringResource(Res.string.main_subtitle),
        ),
        onThemeToggleClick = { onEvent(MainUiEvent.ThemeToggleClicked) },
    )
}

// region Preview

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun MainBeforeWorkoutDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        MainScreen(state = MainPreviewData.beforeWorkout, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun MainBeforeWorkoutLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        MainScreen(state = MainPreviewData.beforeWorkout, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun MainAfterWorkoutDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        MainScreen(state = MainPreviewData.afterWorkout, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun MainAfterWorkoutLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        MainScreen(state = MainPreviewData.afterWorkout, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun MainLoadingDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        MainScreen(state = MainUiState.Loading, onEvent = {})
    }
}

// endregion
