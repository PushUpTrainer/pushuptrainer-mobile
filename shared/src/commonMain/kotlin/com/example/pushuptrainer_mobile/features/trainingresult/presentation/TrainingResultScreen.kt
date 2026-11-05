package com.example.pushuptrainer_mobile.features.trainingresult.presentation

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pushuptrainer_mobile.core.uikit.components.AppBottomBar
import com.example.pushuptrainer_mobile.core.uikit.components.AppBottomBarDestination
import com.example.pushuptrainer_mobile.core.uikit.components.AppPrimaryButton
import com.example.pushuptrainer_mobile.core.uikit.components.AppSecondaryButton
import com.example.pushuptrainer_mobile.core.uikit.components.StreakCard
import com.example.pushuptrainer_mobile.core.uikit.components.ThemeToggleButton
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import com.example.pushuptrainer_mobile.features.trainingresult.presentation.components.TrainingResultSummaryCard
import com.example.pushuptrainer_mobile.features.trainingresult.presentation.components.TrainingSavedCard
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.training_result_go_to_main
import pushuptrainermobile.shared.generated.resources.training_result_start_new
import pushuptrainermobile.shared.generated.resources.training_result_subtitle_goal_reached
import pushuptrainermobile.shared.generated.resources.training_result_subtitle_no_goal
import pushuptrainermobile.shared.generated.resources.training_result_subtitle_stopped_early
import pushuptrainermobile.shared.generated.resources.training_result_title

/** Анимация появления результата из макета: задержка 140 мс, 360 мс, ease-out. */
private const val AppearanceDelayMillis = 140L
private const val AppearanceDurationMillis = 360

/** Точка входа экрана для продакшена: берёт состояние из ViewModel. */
@Composable
fun TrainingResultRoute(
    viewModel: TrainingResultViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TrainingResultScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

/**
 * Экран результата тренировки (макеты 1.3.1–1.3.3, состояния A «Появление» и B «Результат»).
 *
 * @param playAppearanceAnimation проигрывать ли анимацию появления результата.
 * В Preview передаётся `false`, чтобы сразу показать итоговое состояние.
 */
@Composable
fun TrainingResultScreen(
    state: TrainingResultUiState,
    onEvent: (TrainingResultUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    playAppearanceAnimation: Boolean = true,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .safeDrawingPadding(),
    ) {
        when (state) {
            TrainingResultUiState.Loading -> TrainingResultLoading(
                onEvent = onEvent,
                modifier = Modifier.weight(1f),
            )

            is TrainingResultUiState.Content -> TrainingResultContent(
                state = state,
                onEvent = onEvent,
                playAppearanceAnimation = playAppearanceAnimation,
                modifier = Modifier.weight(1f),
            )
        }
        AppBottomBar(
            selected = AppBottomBarDestination.Trainer,
            onDestinationClick = { onEvent(TrainingResultUiEvent.BottomBarDestinationClicked(it)) },
        )
    }
}

@Composable
private fun TrainingResultContent(
    state: TrainingResultUiState.Content,
    onEvent: (TrainingResultUiEvent) -> Unit,
    playAppearanceAnimation: Boolean,
    modifier: Modifier = Modifier,
) {
    // Состояние анимации чисто визуальное, поэтому живёт в Compose, а не в UiState.
    var isRevealed by rememberSaveable { mutableStateOf(!playAppearanceAnimation) }
    LaunchedEffect(Unit) {
        if (!isRevealed) {
            delay(AppearanceDelayMillis)
            isRevealed = true
        }
    }
    val appearance by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0f,
        animationSpec = tween(AppearanceDurationMillis, easing = EaseOut),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        TrainingResultHeader(
            subtitle = stringResource(state.summary.outcome.subtitleText()),
            onThemeToggleClick = { onEvent(TrainingResultUiEvent.ThemeToggleClicked) },
        )
        Spacer(Modifier.height(20.dp))
        TrainingResultSummaryCard(
            model = state.summary,
            iconAppearance = appearance,
        )
        Spacer(Modifier.height(24.dp))
        TrainingSavedCard(
            modifier = Modifier.graphicsLayer { alpha = appearance },
        )
        Spacer(Modifier.height(16.dp))
        StreakCard(
            model = state.streak,
            isHighlighted = isRevealed,
        )
        Spacer(Modifier.height(40.dp))
        AppPrimaryButton(
            text = stringResource(Res.string.training_result_start_new),
            onClick = { onEvent(TrainingResultUiEvent.StartNewTrainingClicked) },
        )
        Spacer(Modifier.height(16.dp))
        AppSecondaryButton(
            text = stringResource(Res.string.training_result_go_to_main),
            onClick = { onEvent(TrainingResultUiEvent.GoToMainClicked) },
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TrainingResultLoading(
    onEvent: (TrainingResultUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        TrainingResultHeader(
            subtitle = null,
            onThemeToggleClick = { onEvent(TrainingResultUiEvent.ThemeToggleClicked) },
        )
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
private fun TrainingResultHeader(
    subtitle: String?,
    onThemeToggleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.training_result_title),
                style = AppTheme.typography.screenTitle,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            ThemeToggleButton(onClick = onThemeToggleClick)
        }
        if (subtitle != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

private fun TrainingOutcomeUiModel.subtitleText() = when (this) {
    TrainingOutcomeUiModel.NoGoal -> Res.string.training_result_subtitle_no_goal
    is TrainingOutcomeUiModel.GoalReached -> Res.string.training_result_subtitle_goal_reached
    is TrainingOutcomeUiModel.StoppedEarly -> Res.string.training_result_subtitle_stopped_early
}

// region Preview

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun TrainingResultNoGoalLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        TrainingResultScreen(
            state = TrainingResultPreviewData.noGoal,
            onEvent = {},
            playAppearanceAnimation = false,
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun TrainingResultNoGoalDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        TrainingResultScreen(
            state = TrainingResultPreviewData.noGoal,
            onEvent = {},
            playAppearanceAnimation = false,
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun TrainingResultGoalReachedLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        TrainingResultScreen(
            state = TrainingResultPreviewData.goalReached,
            onEvent = {},
            playAppearanceAnimation = false,
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun TrainingResultGoalReachedDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        TrainingResultScreen(
            state = TrainingResultPreviewData.goalReached,
            onEvent = {},
            playAppearanceAnimation = false,
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun TrainingResultStoppedEarlyLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        TrainingResultScreen(
            state = TrainingResultPreviewData.stoppedEarly,
            onEvent = {},
            playAppearanceAnimation = false,
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun TrainingResultStoppedEarlyDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        TrainingResultScreen(
            state = TrainingResultPreviewData.stoppedEarly,
            onEvent = {},
            playAppearanceAnimation = false,
        )
    }
}

/** Состояние A «Появление» — до анимации: без иконки, карточки сохранения и подсветки серии. */
@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun TrainingResultAppearanceStartPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        TrainingResultScreen(
            state = TrainingResultPreviewData.goalReached,
            onEvent = {},
            playAppearanceAnimation = true,
        )
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun TrainingResultLoadingPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        TrainingResultScreen(
            state = TrainingResultUiState.Loading,
            onEvent = {},
        )
    }
}

// endregion
