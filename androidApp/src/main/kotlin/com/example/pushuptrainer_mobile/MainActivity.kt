package com.example.pushuptrainer_mobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import app.pushups.contract.PoseListener
import app.pushups.presentation.workout.CameraPreview
import app.pushups.presentation.workout.CameraPreviewHandle
import app.pushups.presentation.workout.WorkoutScreen
import app.pushups.presentation.workout.WorkoutViewModel

class MainActivity : ComponentActivity() {
    private val workout = WorkoutViewModel()
    private val preview by lazy { PreviewView(this) }
    private var source: AndroidPoseSource? = null
    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera() else workout.onCameraError("Разрешите доступ к камере")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val previewHandle = remember { CameraPreviewHandle(preview) }
            WorkoutScreen(
                state = workout.state,
                onEvent = workout::onEvent,
                cameraPreview = { CameraPreview(previewHandle, Modifier.fillMaxSize()) },
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            permission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        if (source != null) return
        val poseSource = AndroidPoseSource(this, preview, workout::onCameraError)
        source = poseSource
        poseSource.start(PoseListener(workout::onFrame))
    }

    override fun onStop() {
        source?.stop()
        source = null
        super.onStop()
    }
}
