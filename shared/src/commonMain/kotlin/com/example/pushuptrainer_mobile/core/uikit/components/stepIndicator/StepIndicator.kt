package com.example.pushuptrainer_mobile.core.uikit.components.stepIndicator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme

private val DotSize = 6.dp
private val ActiveDotWidth = 20.dp
private val DotSpacing = 8.dp
private const val INACTIVE_DOT_ALPHA = 0.35f

/** Индикатор шагов: точки, текущий шаг — вытянутая акцентная полоска. */
@Composable
fun StepIndicator(
    model: StepIndicatorUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DotSpacing),
    ) {
        repeat(model.stepCount) { index ->
            StepDot(isActive = index == model.currentStepIndex)
        }
    }
}

@Composable
private fun StepDot(isActive: Boolean) {
    val colors = AppTheme.colors
    val width by animateDpAsState(if (isActive) ActiveDotWidth else DotSize)
    val color by animateColorAsState(
        if (isActive) colors.accent else colors.textSecondary.copy(alpha = INACTIVE_DOT_ALPHA),
    )
    Box(
        modifier = Modifier
            .width(width)
            .height(DotSize)
            .clip(CircleShape)
            .background(color),
    )
}

@Preview
@Composable
private fun StepIndicatorDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            StepIndicator(
                model = StepIndicatorUiModel(stepCount = 4, currentStepIndex = 1),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun StepIndicatorLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            StepIndicator(
                model = StepIndicatorUiModel(stepCount = 4, currentStepIndex = 3),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
