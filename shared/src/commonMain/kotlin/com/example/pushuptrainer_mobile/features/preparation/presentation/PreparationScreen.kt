package com.example.pushuptrainer_mobile.features.preparation.presentation

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationBar
import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination
import com.example.pushuptrainer_mobile.core.uikit.components.goalSelectorCard.GoalSelectorCard
import com.example.pushuptrainer_mobile.core.uikit.components.howToPlacePhoneRow.HowToPlacePhoneRow
import com.example.pushuptrainer_mobile.core.uikit.components.noGoalCard.NoGoalCard
import com.example.pushuptrainer_mobile.core.uikit.components.poseIllustration.PoseIllustration
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationMode
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationModeSwitch
import com.example.pushuptrainer_mobile.core.uikit.components.primaryButton.AppPrimaryButton
import com.example.pushuptrainer_mobile.core.uikit.components.themeToggleButton.ThemeToggleButton
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.preparation_start
import pushuptrainermobile.shared.generated.resources.preparation_subtitle
import pushuptrainermobile.shared.generated.resources.preparation_title

/** Точка входа экрана для продакшена: берёт состояние из ViewModel. */
@Composable
fun PreparationRoute(
    viewModel: PreparationViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PreparationScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

/** Экран подготовки к тренировке (макеты 1.1.1 «Без цели», 1.1.2 «С целью»). */
@Composable
fun PreparationScreen(
    state: PreparationUiState,
    onEvent: (PreparationUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .safeDrawingPadding(),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            PreparationHeader(
                onThemeToggleClick = { onEvent(PreparationUiEvent.ThemeToggleClicked) },
            )
            Spacer(Modifier.height(20.dp))
            PreparationModeSwitch(
                selectedMode = state.selectedMode,
                onModeSelect = { onEvent(PreparationUiEvent.ModeSelected(it)) },
            )
            Spacer(Modifier.height(16.dp))
            when (state.selectedMode) {
                PreparationMode.WithGoal -> GoalSelectorCard(
                    model = state.goal,
                    onDecreaseClick = { onEvent(PreparationUiEvent.GoalDecreaseClicked) },
                    onIncreaseClick = { onEvent(PreparationUiEvent.GoalIncreaseClicked) },
                )

                PreparationMode.WithoutGoal -> NoGoalCard()
            }
            Spacer(Modifier.height(16.dp))
            HowToPlacePhoneRow(
                onClick = { onEvent(PreparationUiEvent.HowToPlacePhoneClicked) },
            )
            Spacer(Modifier.height(16.dp))
            PoseIllustration()
            Spacer(Modifier.height(24.dp))
            AppPrimaryButton(
                text = stringResource(Res.string.preparation_start),
                onClick = { onEvent(PreparationUiEvent.StartTrainingClicked) },
            )
            Spacer(Modifier.height(16.dp))
        }
        BottomNavigationBar(
            selected = BottomNavigationDestination.Trainer,
            onDestinationClick = { onEvent(PreparationUiEvent.BottomBarDestinationClicked(it)) },
        )
    }
}

@Composable
private fun PreparationHeader(
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
                text = stringResource(Res.string.preparation_title),
                style = AppTheme.typography.screenTitle,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            ThemeToggleButton(
                isDarkTheme = AppTheme.isDarkTheme,
                onClick = onThemeToggleClick,
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.preparation_subtitle),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
        )
    }
}

// region Preview

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun PreparationWithGoalLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        PreparationScreen(state = PreparationPreviewData.withGoal, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun PreparationWithGoalDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        PreparationScreen(state = PreparationPreviewData.withGoal, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun PreparationWithoutGoalLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        PreparationScreen(state = PreparationPreviewData.withoutGoal, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun PreparationWithoutGoalDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        PreparationScreen(state = PreparationPreviewData.withoutGoal, onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun PreparationGoalAtMinimumPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        PreparationScreen(state = PreparationPreviewData.goalAtMinimum, onEvent = {})
    }
}

// endregion
