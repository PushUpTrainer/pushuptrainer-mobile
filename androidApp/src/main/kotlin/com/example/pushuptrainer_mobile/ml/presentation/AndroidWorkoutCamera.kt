package com.example.pushuptrainer_mobile.ml.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.pushuptrainer_mobile.ml.data.mediapipe.AndroidPoseSource

@Composable
fun AndroidWorkoutCamera(listener: WorkoutCameraListener) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    var granted by remember(listener) {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
        granted = allowed
        if (!allowed) listener.onError()
    }
    LaunchedEffect(listener) {
        if (!granted) permission.launch(Manifest.permission.CAMERA)
    }
    if (granted) {
        val preview = remember(listener, context) { PreviewView(context).apply { implementationMode = PreviewView.ImplementationMode.COMPATIBLE } }
        AndroidView(factory = { preview }, modifier = Modifier.fillMaxSize())
        DisposableEffect(preview, owner, listener) {
            val source = AndroidPoseSource(owner, preview, { listener.onError() }, { listener.onReady() })
            var stopped = false
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> if (!stopped) source.start(listener)
                    Lifecycle.Event.ON_STOP -> {
                        stopped = true
                        source.stop()
                    }
                    else -> Unit
                }
            }
            owner.lifecycle.addObserver(observer)
            onDispose {
                owner.lifecycle.removeObserver(observer)
                source.stop()
            }
        }
    }
}
