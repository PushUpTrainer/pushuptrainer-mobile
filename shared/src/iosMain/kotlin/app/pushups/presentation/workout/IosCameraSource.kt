package app.pushups.presentation.workout

import app.pushups.contract.PoseSource
import platform.AVFoundation.AVCaptureSession

interface IosCameraSource : PoseSource {
    val session: AVCaptureSession
    fun setErrorListener(listener: (String) -> Unit)
}
