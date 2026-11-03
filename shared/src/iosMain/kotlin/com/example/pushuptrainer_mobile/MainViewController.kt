package com.example.pushuptrainer_mobile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import app.pushups.contract.PoseListener
import app.pushups.presentation.workout.CameraPreview
import app.pushups.presentation.workout.CameraPreviewHandle
import app.pushups.presentation.workout.IosCameraSource
import app.pushups.presentation.workout.WorkoutScreen
import app.pushups.presentation.workout.WorkoutViewModel

fun MainViewController(source: IosCameraSource) = ComposeUIViewController {
    val viewModel = remember { WorkoutViewModel() }
    val previewHandle = remember(source) { CameraPreviewHandle(source.session) }

    DisposableEffect(source) {
        source.setErrorListener(viewModel::onCameraError)
        source.start(PoseListener(viewModel::onFrame))
        onDispose { source.stop() }
    }

    WorkoutScreen(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        cameraPreview = { CameraPreview(previewHandle, Modifier.fillMaxSize()) },
    )
}
