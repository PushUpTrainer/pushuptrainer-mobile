package com.example.pushuptrainer_mobile.ml.domain.analysis

import com.example.pushuptrainer_mobile.ml.domain.model.Hint
import com.example.pushuptrainer_mobile.ml.domain.model.Landmark
import com.example.pushuptrainer_mobile.ml.domain.model.Phase
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrame
import com.example.pushuptrainer_mobile.ml.domain.model.TechniqueError
import kotlin.math.abs
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GoldenReplayTest {
    @Test
    fun allFramesMatchPythonReference() {
        for (name in listOf("synthetic_edge_cases", "webcam_20261017_115256", "webcam_20261017_120154")) {
            val input = lines("samples/$name.csv")
            val expected = lines("golden/$name.expected.csv")
            assertEquals(input.size, expected.size, name)
            val counter = PushupCounter()
            for (index in input.indices) {
                val row = input[index].split(',')
                val gold = expected[index].split(',')
                val points = if (row[5] == "0") null else (0 until 12).map { point ->
                    val offset = 6 + point * 5
                    Landmark(row[offset].toFloat(), row[offset + 1].toFloat(), row[offset + 2].toFloat(),
                        row[offset + 3].toFloat(), row[offset + 4].toFloat())
                }
                val state = counter.onFrame(PoseFrame(row[2].toLong(), row[3].toInt(), row[4].toInt(), points))
                val label = "$name frame ${row[1]}"
                assertEquals(row[1], gold[0], label)
                assertEquals(row[2], gold[1], label)
                assertEquals(Phase.valueOf(gold[5]), state.phase, label)
                assertEquals(gold[6].toInt(), state.reps, label)
                assertEquals(gold[7].takeIf { it.isNotEmpty() }?.let(Hint::valueOf), state.hint, label)
                assertEquals(gold[9].takeIf { it.isNotEmpty() }?.split('|')?.map(TechniqueError::valueOf)?.toSet() ?: emptySet(), state.errors, label)
                assertEquals(gold[10].toInt(), counter.snapshot.errorCounts[TechniqueError.HANDS_TOO_WIDE] ?: 0, label)
                assertEquals(gold[11].toInt(), counter.snapshot.errorCounts[TechniqueError.HANDS_TOO_NARROW] ?: 0, label)
                closeTo(gold[2], counter.snapshot.signal, label)
                closeTo(gold[3], counter.snapshot.smoothed, label)
                closeTo(gold[4], counter.snapshot.top, label)
                closeTo(gold[8], counter.snapshot.grip, label)
            }
            val summary = counter.finish()
            val last = expected.last().split(',')
            assertEquals(last[6].toInt(), summary.reps, name)
            assertEquals(last[10].toInt(), summary.errorCounts[TechniqueError.HANDS_TOO_WIDE] ?: 0, name)
            assertEquals(last[11].toInt(), summary.errorCounts[TechniqueError.HANDS_TOO_NARROW] ?: 0, name)
        }
    }

    private fun lines(path: String) =
        checkNotNull(javaClass.classLoader?.getResourceAsStream(path)) { path }.bufferedReader().use { it.readLines().drop(1) }

    private fun closeTo(expected: String, actual: Double?, label: String) {
        if (expected.isEmpty()) {
            assertEquals(null, actual, label)
        } else {
            assertTrue(actual != null && abs(actual - expected.toDouble()) <= 0.0001 * max(1.0, abs(expected.toDouble())),
                "$label: expected $expected, actual $actual")
        }
    }
}
