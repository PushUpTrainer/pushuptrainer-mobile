package com.example.pushuptrainer_mobile.ml.data.mediapipe

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.pushuptrainer_mobile.ml.domain.model.BodyPoint
import com.example.pushuptrainer_mobile.ml.domain.model.Landmark
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrame
import com.example.pushuptrainer_mobile.ml.domain.model.PoseListener
import com.example.pushuptrainer_mobile.ml.domain.model.PoseSource
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import java.util.concurrent.Executors

class AndroidPoseSource(
    private val owner: LifecycleOwner,
    private val previewView: PreviewView,
    private val onError: (String) -> Unit,
) : PoseSource {
    private val context = previewView.context
    private val mainExecutor = ContextCompat.getMainExecutor(context)
    @Volatile private var session: Session? = null

    private class Session {
        val executor = Executors.newSingleThreadExecutor()
        var cameraProvider: ProcessCameraProvider? = null
        var preview: Preview? = null
        var analysis: ImageAnalysis? = null
        var landmarker: PoseLandmarker? = null
        var lastInputTimestamp = 0L
    }

    override fun start(listener: PoseListener) {
        if (session != null) return
        val current = Session()
        session = current
        try {
            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(BaseOptions.builder().setModelAssetPath("pose_landmarker_full.task").build())
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumPoses(1)
                .setMinPoseDetectionConfidence(0.5f)
                .setMinPosePresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setResultListener { result, image ->
                    val detected = result.landmarks().firstOrNull()
                    val points = detected?.let { pose ->
                        BodyPoint.entries.map { point ->
                            val landmark = pose[point.mediaPipeIndex]
                            Landmark(landmark.x(), landmark.y(), landmark.z(),
                                landmark.visibility().orElse(0f), landmark.presence().orElse(0f))
                        }
                    }
                    val frame = PoseFrame(result.timestampMs(), image.width, image.height, points)
                    mainExecutor.execute { if (session === current) listener.onFrame(frame) }
                }
                .setErrorListener { error -> mainExecutor.execute { if (session === current) onError(error.message ?: "Ошибка модели") } }
                .build()
            current.landmarker = PoseLandmarker.createFromOptions(context, options)
            val future = ProcessCameraProvider.getInstance(context)
            future.addListener({
                if (session !== current) return@addListener
                try {
                    val provider = future.get()
                    current.cameraProvider = provider
                    val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
                    val analysis = ImageAnalysis.Builder()
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    current.preview = preview
                    current.analysis = analysis
                    analysis.setAnalyzer(current.executor) { image -> analyze(image, current) }
                    provider.bindToLifecycle(owner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, analysis)
                } catch (error: Exception) {
                    stop()
                    onError(error.message ?: "Камера недоступна")
                }
            }, mainExecutor)
        } catch (error: Exception) {
            stop()
            onError(error.message ?: "Модель не запустилась")
        }
    }

    private fun analyze(image: ImageProxy, current: Session) {
        try {
            if (session !== current) return
            val bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
            image.planes[0].buffer.rewind()
            bitmap.copyPixelsFromBuffer(image.planes[0].buffer)
            val rotation = image.imageInfo.rotationDegrees
            val upright = if (rotation == 0) bitmap else Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height,
                Matrix().apply { postRotate(rotation.toFloat()) }, true,
            )
            val timestamp = maxOf(image.imageInfo.timestamp / 1_000_000, current.lastInputTimestamp + 1)
            current.lastInputTimestamp = timestamp
            if (session === current) current.landmarker?.detectAsync(BitmapImageBuilder(upright).build(), timestamp)
        } catch (error: Exception) {
            mainExecutor.execute { if (session === current) onError(error.message ?: "Ошибка обработки кадра") }
        } finally {
            image.close()
        }
    }

    override fun stop() {
        val current = session ?: return
        session = null
        try {
            current.analysis?.clearAnalyzer()
            val useCases = listOfNotNull(current.preview, current.analysis).toTypedArray()
            current.cameraProvider?.unbind(*useCases)
        } finally {
            current.executor.execute { current.landmarker?.close() }
            current.executor.shutdown()
        }
    }
}
