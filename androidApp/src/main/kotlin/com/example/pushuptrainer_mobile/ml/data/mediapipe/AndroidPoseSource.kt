package com.example.pushuptrainer_mobile.ml.data.mediapipe

import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.os.SystemClock
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.UseCaseGroup
import androidx.core.view.doOnLayout
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.pushuptrainer_mobile.ml.domain.model.BodyPoint
import com.example.pushuptrainer_mobile.ml.domain.model.Landmark
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrame
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrameGate
import com.example.pushuptrainer_mobile.ml.domain.model.PoseListener
import com.example.pushuptrainer_mobile.ml.domain.model.PoseSource
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import java.util.concurrent.Executors

@OptIn(ExperimentalCamera2Interop::class)
class AndroidPoseSource(
    private val owner: LifecycleOwner,
    private val previewView: PreviewView,
    private val onError: (String) -> Unit,
    private val onReady: () -> Unit,
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
        var frameGate: PoseFrameGate? = null
        var usesRealtimeTimestamps = false
        var preparationStreamReplaced = false
        var replacementRequested = false
        var readyRequested = false
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
                    val analysis = createAnalysis(current)
                    current.preview = preview
                    current.analysis = analysis
                    previewView.doOnLayout {
                        if (session === current) {
                            try {
                                val group = UseCaseGroup.Builder()
                                    .addUseCase(preview).addUseCase(analysis)
                                    .setViewPort(requireNotNull(previewView.viewPort)).build()
                                val camera = provider.bindToLifecycle(owner, CameraSelector.DEFAULT_FRONT_CAMERA, group)
                                val timestampSource = Camera2CameraInfo.from(camera.cameraInfo)
                                    .getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE)
                                synchronized(current) {
                                    current.usesRealtimeTimestamps = timestampSource == CameraCharacteristics.SENSOR_INFO_TIMESTAMP_SOURCE_REALTIME
                                }
                            } catch (error: Exception) {
                                stop()
                                onError(error.message ?: "Камера недоступна")
                            }
                        }
                    }
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

    private fun createAnalysis(current: Session): ImageAnalysis {
        val analysis = ImageAnalysis.Builder()
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        analysis.setAnalyzer(current.executor) { image -> analyze(image, current, analysis) }
        return analysis
    }

    private fun replacePreparationStream(current: Session, previous: ImageAnalysis) {
        if (session !== current || current.analysis !== previous) return
        try {
            val provider = requireNotNull(current.cameraProvider)
            previous.clearAnalyzer()
            provider.unbind(previous)
            val analysis = createAnalysis(current)
            synchronized(current) {
                current.analysis = analysis
                current.preparationStreamReplaced = true
            }
            val group = UseCaseGroup.Builder()
                .addUseCase(requireNotNull(current.preview)).addUseCase(analysis)
                .setViewPort(requireNotNull(previewView.viewPort)).build()
            provider.bindToLifecycle(owner, CameraSelector.DEFAULT_FRONT_CAMERA, group)
        } catch (error: Exception) {
            stop()
            onError(error.message ?: "Камера недоступна")
        }
    }

    private fun analyze(image: ImageProxy, current: Session, source: ImageAnalysis) {
        try {
            synchronized(current) {
                if (session !== current || current.analysis !== source) return
                val now = SystemClock.elapsedRealtimeNanos()
                val gate = current.frameGate
                if (gate == null) {
                    if (!current.readyRequested) {
                        current.readyRequested = true
                        mainExecutor.execute {
                            synchronized(current) {
                                if (session === current && current.analysis === source) {
                                    current.frameGate = PoseFrameGate(SystemClock.elapsedRealtimeNanos())
                                    onReady()
                                }
                            }
                        }
                    }
                    return
                }
                if (current.usesRealtimeTimestamps) {
                    if (!gate.allows(image.imageInfo.timestamp, now)) return
                } else if (!current.preparationStreamReplaced) {
                    if (gate.allows(now, now) && !current.replacementRequested) {
                        current.replacementRequested = true
                        mainExecutor.execute { replacePreparationStream(current, source) }
                    }
                    return
                }
            }
            val buffer = image.toBitmap()
            val crop = image.cropRect
            val bitmap = Bitmap.createBitmap(buffer, crop.left, crop.top, crop.width(), crop.height())
            val rotation = image.imageInfo.rotationDegrees
            val upright = if (rotation == 0) bitmap else Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height,
                Matrix().apply { postRotate(rotation.toFloat()) }, true,
            )
            val timestamp = maxOf(image.imageInfo.timestamp / 1_000_000, current.lastInputTimestamp + 1)
            current.lastInputTimestamp = timestamp
            synchronized(current) {
                if (session === current && current.analysis === source) current.landmarker?.detectAsync(BitmapImageBuilder(upright).build(), timestamp)
            }
        } catch (error: Exception) {
            mainExecutor.execute { if (session === current) onError(error.message ?: "Ошибка обработки кадра") }
        } finally {
            image.close()
        }
    }

    override fun stop() {
        val current = session ?: return
        synchronized(current) {
            session = null
            current.frameGate = null
        }
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
