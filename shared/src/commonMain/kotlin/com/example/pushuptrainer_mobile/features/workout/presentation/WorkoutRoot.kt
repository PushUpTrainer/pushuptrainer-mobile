package com.example.pushuptrainer_mobile.features.workout.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.pushuptrainer_mobile.core.uikit.components.poseOverlay.PoseOverlay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.ml.presentation.LocalWorkoutCamera
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun WorkoutRoot(
    entry: NavigationEntry,
    goal: Int?,
    viewModel: WorkoutViewModel = koinViewModel(parameters = { parametersOf(entry, goal) }),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> viewModel.onBackground()
                Lifecycle.Event.ON_RESUME -> viewModel.onForeground()
                else -> Unit
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            viewModel.onBackground()
        }
    }
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = true,
        onBackCompleted = {
            viewModel.onEvent(if (state.showFinishConfirmation) WorkoutUiEvent.DismissFinishClicked else WorkoutUiEvent.BackClicked)
        },
    )
    val camera = LocalWorkoutCamera.current
    val listener = remember(viewModel, state.cameraGeneration) { viewModel.cameraListener(state.cameraGeneration) }
    WorkoutScreen(state = state, onEvent = viewModel::onEvent) {
        if (state.status == WorkoutStatus.Starting || state.status == WorkoutStatus.Running) {
            camera(listener)
            val overlay by viewModel.overlay.collectAsStateWithLifecycle()
            overlay?.let { PoseOverlay(it, Modifier.fillMaxSize()) }
        }
    }
}
