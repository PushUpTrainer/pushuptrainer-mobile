package com.example.pushuptrainer_mobile.features.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pushuptrainer_mobile.core.uikit.components.closeButton.CloseButton
import com.example.pushuptrainer_mobile.core.uikit.components.primaryButton.AppPrimaryButton
import com.example.pushuptrainer_mobile.core.uikit.components.secondaryButton.AppSecondaryButton
import com.example.pushuptrainer_mobile.core.uikit.components.stepIndicator.StepIndicator
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.onboarding_appearance_text
import pushuptrainermobile.shared.generated.resources.onboarding_appearance_title
import pushuptrainermobile.shared.generated.resources.onboarding_back
import pushuptrainermobile.shared.generated.resources.onboarding_camera_text
import pushuptrainermobile.shared.generated.resources.onboarding_camera_title
import pushuptrainermobile.shared.generated.resources.onboarding_finish
import pushuptrainermobile.shared.generated.resources.onboarding_modes_title
import pushuptrainermobile.shared.generated.resources.onboarding_modes_with_goal_text
import pushuptrainermobile.shared.generated.resources.onboarding_modes_with_goal_title
import pushuptrainermobile.shared.generated.resources.onboarding_modes_without_goal_text
import pushuptrainermobile.shared.generated.resources.onboarding_modes_without_goal_title
import pushuptrainermobile.shared.generated.resources.onboarding_next
import pushuptrainermobile.shared.generated.resources.onboarding_progress_text
import pushuptrainermobile.shared.generated.resources.onboarding_progress_title
import pushuptrainermobile.shared.generated.resources.onboarding_title

/** Точка входа экрана для продакшена: берёт состояние из ViewModel. */
@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    OnboardingScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

/**
 * Онбординг «Знакомство»: четыре шага с примерами экранов (макеты *4.1–*4.4).
 *
 * Шаги листаются пальцем ([HorizontalPager]) и кнопками. Главный источник правды
 * о текущем шаге — [OnboardingUiState.step]; pager только показывает его и сообщает о свайпах.
 */
@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onEvent: (OnboardingUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(initialPage = state.step.ordinal) { OnboardingStep.entries.size }
    SyncPagerWithStep(
        pagerState = pagerState,
        step = state.step,
        onStepSwiped = { step -> onEvent(OnboardingUiEvent.StepSwiped(step)) },
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
            .safeDrawingPadding(),
    ) {
        OnboardingHeader(onCloseClick = { onEvent(OnboardingUiEvent.CloseClicked) })
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { page ->
            when (OnboardingStep.entries[page]) {
                OnboardingStep.Modes -> ModesPage()
                OnboardingStep.PhonePlacement -> PhonePlacementPage()
                OnboardingStep.Progress -> ProgressPage()
                OnboardingStep.Appearance -> AppearancePage()
            }
        }
        StepIndicator(
            model = state.stepIndicator,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(26.dp))
        OnboardingButtons(
            state = state,
            onEvent = onEvent,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * Синхронизирует pager и шаг из [OnboardingUiState] в обе стороны.
 *
 * - Шаг сменился кнопкой → плавно прокручиваем pager на нужную страницу.
 * - Пользователь долистал до страницы и отпустил палец → сообщаем шаг наружу.
 *
 * Берём [PagerState.settledPage] (страница, на которой pager остановился), а не currentPage:
 * иначе во время анимации с шага 1 на шаг 3 мы бы на миг сообщили «шаг 2».
 */
@Composable
private fun SyncPagerWithStep(
    pagerState: PagerState,
    step: OnboardingStep,
    onStepSwiped: (OnboardingStep) -> Unit,
) {
    // Состояние → pager.
    LaunchedEffect(pagerState, step) {
        if (pagerState.currentPage != step.ordinal) {
            pagerState.animateScrollToPage(step.ordinal)
        }
    }
    // Pager → состояние. rememberUpdatedState нужен, чтобы долгоживущий сбор
    // всегда вызывал свежий callback и не перезапускался при каждой рекомпозиции.
    val currentOnStepSwiped by rememberUpdatedState(onStepSwiped)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            currentOnStepSwiped(OnboardingStep.entries[page])
        }
    }
}

@Composable
private fun OnboardingHeader(onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.onboarding_title),
            style = AppTheme.typography.sectionTitle.copy(fontWeight = FontWeight.Medium),
            color = AppTheme.colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        CloseButton(onClick = onCloseClick)
    }
}

// region Страницы

/** *4.1 «Выберите свой темп». */
@Composable
private fun ModesPage() {
    OnboardingPage(
        title = stringResource(Res.string.onboarding_modes_title),
        illustration = { ModesExample() },
    ) {
        ModeDescription(
            title = Res.string.onboarding_modes_with_goal_title,
            text = Res.string.onboarding_modes_with_goal_text,
        )
        Spacer(Modifier.height(16.dp))
        ModeDescription(
            title = Res.string.onboarding_modes_without_goal_title,
            text = Res.string.onboarding_modes_without_goal_text,
        )
    }
}

/** *4.2 «Поставьте телефон перед собой». */
@Composable
private fun PhonePlacementPage() {
    OnboardingPage(
        title = stringResource(Res.string.onboarding_camera_title),
        illustration = { PhonePlacementExample() },
    ) {
        BodyText(Res.string.onboarding_camera_text)
    }
}

/** *4.3 «Следите за прогрессом». */
@Composable
private fun ProgressPage() {
    OnboardingPage(
        title = stringResource(Res.string.onboarding_progress_title),
        illustration = { ProgressExample() },
        titleTopSpacing = 32.dp,
    ) {
        BodyText(Res.string.onboarding_progress_text)
    }
}

/** *4.4 «Выберите оформление». */
@Composable
private fun AppearancePage() {
    OnboardingPage(
        title = stringResource(Res.string.onboarding_appearance_title),
        illustration = { AppearanceExample() },
        titleTopSpacing = 32.dp,
    ) {
        BodyText(Res.string.onboarding_appearance_text)
    }
}

/**
 * Общая рамка страницы онбординга: иллюстрация сверху, под ней заголовок и описание.
 * Страница прокручивается по вертикали, если не помещается на маленьком экране.
 *
 * @param titleTopSpacing отступ от иллюстрации до заголовка: по макету у шагов 1–2 он 48dp, у 3–4 — 32dp.
 * @param description слот, а не строка: у шага «Режимы» описание из двух блоков с подзаголовками.
 */
@Composable
private fun OnboardingPage(
    title: String,
    illustration: @Composable () -> Unit,
    titleTopSpacing: Dp = 48.dp,
    description: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(40.dp))
        illustration()
        Spacer(Modifier.height(titleTopSpacing))
        Text(
            text = title,
            style = AppTheme.typography.sectionTitle,
            color = AppTheme.colors.textPrimary,
        )
        Spacer(Modifier.height(18.dp))
        description()
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ModeDescription(
    title: StringResource,
    text: StringResource,
) {
    Column {
        Text(
            text = stringResource(title),
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.accentText,
        )
        Spacer(Modifier.height(4.dp))
        BodyText(text)
    }
}

@Composable
private fun BodyText(text: StringResource) {
    Text(
        text = stringResource(text),
        style = AppTheme.typography.body,
        color = AppTheme.colors.textSecondary,
    )
}

// endregion

@Composable
private fun OnboardingButtons(
    state: OnboardingUiState,
    onEvent: (OnboardingUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        if (!state.isFirstStep) {
            AppSecondaryButton(
                text = stringResource(Res.string.onboarding_back),
                onClick = { onEvent(OnboardingUiEvent.BackClicked) },
                modifier = Modifier.width(100.dp),
            )
            Spacer(Modifier.width(12.dp))
        }
        if (state.isLastStep) {
            AppPrimaryButton(
                text = stringResource(Res.string.onboarding_finish),
                onClick = { onEvent(OnboardingUiEvent.FinishClicked) },
                modifier = Modifier.weight(1f),
            )
        } else {
            AppPrimaryButton(
                text = stringResource(Res.string.onboarding_next),
                onClick = { onEvent(OnboardingUiEvent.NextClicked) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// region Preview

@Composable
private fun OnboardingPreview(step: OnboardingStep, darkTheme: Boolean) {
    PushUpTrainerTheme(darkTheme = darkTheme) {
        OnboardingScreen(state = OnboardingUiState(step = step), onEvent = {})
    }
}

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun OnboardingModesDarkPreview() = OnboardingPreview(OnboardingStep.Modes, darkTheme = true)

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun OnboardingPhonePlacementDarkPreview() = OnboardingPreview(OnboardingStep.PhonePlacement, darkTheme = true)

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun OnboardingProgressDarkPreview() = OnboardingPreview(OnboardingStep.Progress, darkTheme = true)

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun OnboardingAppearanceDarkPreview() = OnboardingPreview(OnboardingStep.Appearance, darkTheme = true)

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun OnboardingProgressLightPreview() = OnboardingPreview(OnboardingStep.Progress, darkTheme = false)

@Preview(widthDp = 360, heightDp = 800)
@Composable
private fun OnboardingAppearanceLightPreview() = OnboardingPreview(OnboardingStep.Appearance, darkTheme = false)

// endregion
