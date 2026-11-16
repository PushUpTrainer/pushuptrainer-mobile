package com.example.pushuptrainer_mobile.ml.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.poseOverlay.inViewport
import com.example.pushuptrainer_mobile.ml.domain.model.Landmark
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrame
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PoseOverlayTest {
    private val points = List(12) { Landmark(0.25f, 0.4f, 0f, 1f, 1f) }

    @Test
    fun frontCameraMirrorsOnceAndCenterCropsToPreview() {
        val model = PoseFrame(1, 640, 480, points).toOverlay()!!
        assertEquals(12, model.points.size)
        assertEquals(12, model.segments.size)
        assertEquals(0.75f, model.points.first().x)
        val projected = model.points.first().inViewport(model, 360f, 640f)
        assertEquals(393.33334f, projected.x, 0.001f)
        assertEquals(256f, projected.y, 0.001f)
    }

    @Test
    fun lowConfidenceAndNonfinitePointsNeverProduceLines() {
        val hidden = points.mapIndexed { index, point ->
            when (index) {
                0 -> point.copy(visibility = 0.49f)
                1 -> point.copy(x = Float.NaN)
                else -> point
            }
        }
        val model = PoseFrame(1, 480, 640, hidden).toOverlay()!!
        assertEquals(10, model.points.size)
        assertEquals(7, model.segments.size)
        assertTrue(model.points.all { it.x.isFinite() && it.y.isFinite() })
    }

    @Test
    fun missingPoseOrInvalidDimensionsClearOverlay() {
        assertNull(PoseFrame(1, 480, 640, null).toOverlay())
        assertNull(PoseFrame(1, 0, 640, points).toOverlay())
        assertNull(PoseFrame(1, 480, 640, points.take(5)).toOverlay())
    }
}
