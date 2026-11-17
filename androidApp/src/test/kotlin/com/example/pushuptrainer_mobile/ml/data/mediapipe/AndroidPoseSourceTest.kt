package com.example.pushuptrainer_mobile.ml.data.mediapipe

import android.app.Application
import android.os.Looper
import android.os.SystemClock
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrameGate
import java.lang.reflect.Proxy
import java.time.Duration
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSystemClock

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], manifest = Config.NONE, application = Application::class)
class AndroidPoseSourceTest {
    @Test
    fun preparingAndDelayedFramesAreClosedBeforePixelConversion() = fixture { source, session, analysis ->
        val ready = SystemClock.elapsedRealtimeNanos()
        session.field("frameGate", PoseFrameGate(ready))
        session.field("usesRealtimeTimestamps", true)
        val first = Frame(ready)
        source.analyze(first, session, analysis)
        ShadowSystemClock.advanceBy(Duration.ofMillis(4999))
        val boundary = Frame(SystemClock.elapsedRealtimeNanos())
        source.analyze(boundary, session, analysis)
        ShadowSystemClock.advanceBy(Duration.ofMillis(1))
        val delayed = Frame(ready + 4_999_000_000L)
        source.analyze(delayed, session, analysis)
        for (frame in listOf(first, boundary, delayed)) {
            assertEquals(1, frame.closed)
            assertEquals(0, frame.pixelReads)
        }
        val fresh = Frame(SystemClock.elapsedRealtimeNanos())
        source.analyze(fresh, session, analysis)
        assertEquals(1, fresh.closed)
        assertEquals(1, fresh.pixelReads)
    }

    @Test
    fun stoppedSessionRejectsQueuedFramesAndReadyCallback() {
        var readyCalls = 0
        fixture(onReady = { readyCalls++ }) { source, session, analysis ->
            val initial = Frame(SystemClock.elapsedRealtimeNanos())
            source.analyze(initial, session, analysis)
            source.stop()
            ShadowSystemClock.advanceBy(Duration.ofSeconds(10))
            shadowOf(Looper.getMainLooper()).idle()
            val late = Frame(SystemClock.elapsedRealtimeNanos())
            source.analyze(late, session, analysis)
            assertEquals(0, readyCalls)
            assertEquals(1, late.closed)
            assertEquals(0, late.pixelReads)
        }
    }

    @Test
    fun unknownClockDiscardsPreparationStreamAndCancelledReplacement() = fixture { source, session, analysis ->
        session.field("frameGate", PoseFrameGate(SystemClock.elapsedRealtimeNanos()))
        ShadowSystemClock.advanceBy(Duration.ofSeconds(5))
        val oldStream = Frame(Long.MAX_VALUE)
        source.analyze(oldStream, session, analysis)
        assertEquals(1, oldStream.closed)
        assertEquals(0, oldStream.pixelReads)
        source.stop()
        shadowOf(Looper.getMainLooper()).idle()
    }

    private fun fixture(onReady: () -> Unit = {}, block: (AndroidPoseSource, Any, ImageAnalysis) -> Unit) {
        val owner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry(this)
        }
        val source = AndroidPoseSource(owner, PreviewView(RuntimeEnvironment.getApplication()), {}, onReady)
        val sessionClass = AndroidPoseSource::class.java.declaredClasses.single { it.simpleName == "Session" }
        val session = sessionClass.getDeclaredConstructor().apply { isAccessible = true }.newInstance()
        val analysis = ImageAnalysis.Builder().build()
        source.field("session", session)
        session.field("analysis", analysis)
        try {
            block(source, session, analysis)
        } finally {
            source.stop()
        }
    }

    private fun Any.field(name: String, value: Any) {
        javaClass.getDeclaredField(name).apply { isAccessible = true }.set(this, value)
    }

    private fun AndroidPoseSource.analyze(frame: Frame, session: Any, analysis: ImageAnalysis) {
        javaClass.getDeclaredMethod("analyze", ImageProxy::class.java, session.javaClass, ImageAnalysis::class.java)
            .apply { isAccessible = true }.invoke(this, frame.image, session, analysis)
    }

    private class Frame(timestamp: Long) {
        var closed = 0
        var pixelReads = 0
        private val info = Proxy.newProxyInstance(ImageInfo::class.java.classLoader, arrayOf(ImageInfo::class.java)) { _, method, _ ->
            check(method.name == "getTimestamp")
            timestamp
        } as ImageInfo
        val image = Proxy.newProxyInstance(ImageProxy::class.java.classLoader, arrayOf(ImageProxy::class.java)) { _, method, _ ->
            when (method.name) {
                "getImageInfo" -> info
                "close" -> { closed++; null }
                else -> { pixelReads++; error("Pixel conversion reached") }
            }
        } as ImageProxy
    }
}
