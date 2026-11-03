package app.pushups.presentation.workout

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

actual class CameraPreviewHandle(val previewView: PreviewView)

@Composable
actual fun CameraPreview(handle: CameraPreviewHandle, modifier: Modifier) {
    AndroidView(factory = { handle.previewView }, modifier = modifier)
}
