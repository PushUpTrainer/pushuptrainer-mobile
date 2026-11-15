package com.example.pushuptrainer_mobile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import androidx.compose.ui.window.ComposeUIViewController
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.pushuptrainer_mobile.ml.presentation.IosWorkoutCamera
import com.example.pushuptrainer_mobile.ml.presentation.LocalWorkoutCamera

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
fun MainViewController(cameraFactory: () -> IosWorkoutCamera) = ComposeUIViewController {
    CompositionLocalProvider(LocalWorkoutCamera provides { listener ->
        val camera = remember(listener) { cameraFactory() }
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        UIKitView(
            factory = { camera.view },
            modifier = Modifier.fillMaxSize(),
            properties = UIKitInteropProperties(interactionMode = null),
        )
        DisposableEffect(camera, listener, lifecycle) {
            var stopped = false
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> if (!stopped) camera.start(listener)
                    Lifecycle.Event.ON_STOP -> {
                        stopped = true
                        camera.stop()
                    }
                    else -> Unit
                }
            }
            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
                camera.stop()
            }
        }
    }) {
        App()
    }
}
