package com.example.pushuptrainer_mobile.features.workout.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.components.primaryButton.AppPrimaryButton
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.features.workout.presentation.components.WorkoutCounter
import com.example.pushuptrainer_mobile.features.workout.presentation.components.WorkoutHint
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.workout_finish
import pushuptrainermobile.shared.generated.resources.workout_finish_early

@Composable
fun WorkoutScreen(
    state: WorkoutUiState,
    onEvent: (WorkoutUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    cameraContent: @Composable BoxScope.() -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Box(modifier = Modifier.matchParentSize(), content = cameraContent)
        BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                WorkoutCounter(
                    model = state.counter,
                    modifier = Modifier.widthIn(max = 240.dp).fillMaxWidth(),
                )
                Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                    WorkoutHint(state.hint, Modifier.fillMaxWidth())
                    Spacer(Modifier.height(32.dp))
                    AppPrimaryButton(
                        text = stringResource(
                            if (state.counter is WorkoutCounterUiModel.Goal) Res.string.workout_finish_early
                            else Res.string.workout_finish,
                        ),
                        onClick = { onEvent(WorkoutUiEvent.FinishClicked) },
                    )
                }
            }
        }
    }
}
