package com.example.pushuptrainer_mobile.features.preparation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pushuptrainer_mobile.LocalToggleTheme
import org.koin.compose.viewmodel.koinViewModel
import com.example.pushuptrainer_mobile.core.uikit.components.goalSelectorCard.GoalSelectorCard
import com.example.pushuptrainer_mobile.core.uikit.components.howToPlacePhoneHint.HowToPlacePhoneHint
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

@Composable
internal fun PreparationRoot(viewModel: PreparationViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PreparationScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onThemeToggle = LocalToggleTheme.current,
    )
}

/** Экран подготовки к тренировке (макеты 1.1.1 «Без цели», 1.1.2 «С целью», 1.1.0 подсказка). */
@Composable
fun PreparationScreen(
    state: PreparationUiState,
    onEvent: (PreparationUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    onThemeToggle: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                PreparationHeader(
                    onThemeToggleClick = onThemeToggle,
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
                    enabled = false,
                    onClick = {},
                )
                Spacer(Modifier.height(16.dp))
            }

        }

        if (state.isHowToPlacePhoneVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {}
                    .safeDrawingPadding()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                HowToPlacePhoneHint(
                    onConfirmClick = { onEvent(PreparationUiEvent.HowToPlacePhoneDismissed) },
                )
            }
        }
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

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun PreparationHintOpenPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        PreparationScreen(state = PreparationPreviewData.hintVisible, onEvent = {})
    }
}

// endregion
