package app.pushups.presentation.workout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIView

actual class CameraPreviewHandle(val session: AVCaptureSession)

@Composable
actual fun CameraPreview(handle: CameraPreviewHandle, modifier: Modifier) {
    UIKitView(factory = { IosCameraPreviewView(handle.session) }, modifier = modifier)
}

@OptIn(ExperimentalForeignApi::class)
private class IosCameraPreviewView(session: AVCaptureSession) : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    private val previewLayer = AVCaptureVideoPreviewLayer().apply {
        this.session = session
        videoGravity = AVLayerVideoGravityResizeAspectFill
    }

    init {
        layer.addSublayer(previewLayer)
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        previewLayer.frame = bounds
    }
}
