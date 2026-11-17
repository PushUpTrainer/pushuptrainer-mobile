package com.example.pushuptrainer_mobile.ml

import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrameGate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PoseFrameGateTest {
    @Test
    fun noDetectionBeforeDeadlineAndOnlyFreshFramesAfterIt() {
        val ready = 12_000_000_000L
        val gate = PoseFrameGate(ready)
        var detectionCalls = 0
        val detectedFrames = mutableListOf<Long>()
        fun submit(captured: Long, delivered: Long) {
            if (gate.allows(captured, delivered)) {
                detectionCalls++
                detectedFrames += captured
            }
        }

        for (elapsed in listOf(0L, 1_000_000_000L, 4_999_000_000L, 4_999_999_999L)) {
            submit(ready + elapsed, ready + elapsed)
        }
        assertEquals(0, detectionCalls)
        submit(ready + 4_999_999_999L, ready + 8_000_000_000L)
        assertEquals(0, detectionCalls)
        submit(ready + 5_000_000_000L, ready + 5_000_000_000L)
        submit(ready + 6_000_000_000L, ready + 6_010_000_000L)
        assertEquals(2, detectionCalls)
        assertEquals(listOf(ready + 5_000_000_000L, ready + 6_000_000_000L), detectedFrames)
    }

    @Test
    fun captureTimestampCannotOpenGateBeforeMonotonicDeadline() {
        val gate = PoseFrameGate(0L)
        assertFalse(gate.allows(5_000_000_000L, 4_999_999_999L))
        assertFalse(gate.allows(6_000_000_000L, 5_000_000_000L))
        assertTrue(gate.allows(5_000_000_000L, 5_000_000_000L))
    }

    @Test
    fun newCameraSessionRejectsFramesCapturedBeforeItsOwnDeadline() {
        val first = PoseFrameGate(0L)
        assertTrue(first.allows(6_000_000_000L, 6_000_000_000L))
        val resumed = PoseFrameGate(20_000_000_000L)
        assertFalse(resumed.allows(6_000_000_000L, 26_000_000_000L))
        assertFalse(resumed.allows(24_999_999_999L, 26_000_000_000L))
        assertTrue(resumed.allows(25_000_000_000L, 25_000_000_000L))
    }
}
