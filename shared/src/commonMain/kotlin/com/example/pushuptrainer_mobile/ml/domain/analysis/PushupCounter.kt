package com.example.pushuptrainer_mobile.ml.domain.analysis

import com.example.pushuptrainer_mobile.ml.domain.model.BodyPoint
import com.example.pushuptrainer_mobile.ml.domain.model.Hint
import com.example.pushuptrainer_mobile.ml.domain.model.Landmark
import com.example.pushuptrainer_mobile.ml.domain.model.Phase
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrame
import com.example.pushuptrainer_mobile.ml.domain.model.PushupAnalyzer
import com.example.pushuptrainer_mobile.ml.domain.model.PushupState
import com.example.pushuptrainer_mobile.ml.domain.model.TechniqueError
import com.example.pushuptrainer_mobile.ml.domain.model.WorkoutSummary
import kotlin.math.hypot

internal data class CounterSnapshot(
    val signal: Double?,
    val smoothed: Double?,
    val top: Double?,
    val grip: Double?,
    val errorCounts: Map<TechniqueError, Int>,
)

class PushupCounter : PushupAnalyzer {
    private val signalWindow = ArrayDeque<Double>()
    private val topWindow = ArrayDeque<Double>()
    private val gripWindow = ArrayDeque<Double>()
    private val repGrips = mutableListOf<Double>()
    private val errorCounts = mutableMapOf<TechniqueError, Int>()
    private var firstTimestamp: Long? = null
    private var lastTimestamp: Long? = null
    private var missingGrip = 0
    private var lowest: Double? = null
    private var phase = Phase.UP
    private var reps = 0

    internal var snapshot = CounterSnapshot(null, null, null, null, emptyMap())
        private set

    override fun onFrame(frame: PoseFrame): PushupState {
        require(frame.imageWidth > 0 && frame.imageHeight > 0)
        require(frame.landmarks == null || frame.landmarks.size == BodyPoint.entries.size)
        require(lastTimestamp == null || frame.timestampMs > lastTimestamp!!)
        if (firstTimestamp == null) firstTimestamp = frame.timestampMs
        lastTimestamp = frame.timestampMs

        val points = frame.landmarks
        val signal = shoulderHeight(points, frame.imageWidth, frame.imageHeight)
        val grip = gripRatio(points, frame.imageWidth, frame.imageHeight)
        if (grip != null) {
            missingGrip = 0
            push(gripWindow, grip, 15)
            if (phase == Phase.DOWN) repGrips.add(grip)
        } else {
            missingGrip++
            if (missingGrip >= 15) gripWindow.clear()
        }
        val errors = if (gripWindow.size >= 5) gripErrors(median(gripWindow)) else emptySet()

        var smoothed = snapshot.smoothed
        var top = snapshot.top
        if (signal != null) {
            push(signalWindow, signal, 5)
            smoothed = median(signalWindow)
            push(topWindow, smoothed, 150)
            top = topWindow.maxOrNull()!!
            if (phase == Phase.UP) {
                if (smoothed < 0.55 * top) {
                    phase = Phase.DOWN
                    lowest = smoothed
                    repGrips.clear()
                    if (grip != null) repGrips.add(grip)
                }
            } else {
                lowest = minOf(lowest!!, smoothed)
                if (smoothed > 0.85 * top) {
                    phase = Phase.UP
                    if (lowest!! < 0.55 * top) {
                        reps++
                        val repErrors = if (repGrips.size >= 5) gripErrors(median(repGrips)) else emptySet()
                        for (error in repErrors) errorCounts[error] = (errorCounts[error] ?: 0) + 1
                    }
                }
            }
        }
        snapshot = CounterSnapshot(signal, smoothed, top, grip, errorCounts.toMap())
        return PushupState(reps, phase, hint(points, frame.imageWidth, frame.imageHeight), errors)
    }

    override fun finish() = WorkoutSummary(
        reps,
        if (firstTimestamp == null) 0 else lastTimestamp!! - firstTimestamp!!,
        errorCounts.toMap(),
    )

    private fun shoulderHeight(points: List<Landmark>?, width: Int, height: Int): Double? {
        if (points == null || !visible(points, 0, 1, 4, 5)) return null
        val shoulderWidth = distance(points, 0, 1, width, height)
        if (shoulderWidth < 1) return null
        val shouldersY = (points[0].y.toDouble() + points[1].y.toDouble()) / 2 * height
        val wristsY = (points[4].y.toDouble() + points[5].y.toDouble()) / 2 * height
        return (wristsY - shouldersY) / shoulderWidth
    }

    private fun gripRatio(points: List<Landmark>?, width: Int, height: Int): Double? {
        if (points == null || !visible(points, 0, 1, 4, 5)) return null
        val shoulderWidth = distance(points, 0, 1, width, height)
        if (shoulderWidth < 1 || shoulderWidth / width < 0.12) return null
        return distance(points, 4, 5, width, height) / shoulderWidth
    }

    private fun hint(points: List<Landmark>?, width: Int, height: Int): Hint? {
        if (points == null) return Hint.NO_PERSON
        if (!visible(points, 0, 1, 2, 3, 4, 5)) return Hint.ARMS_HIDDEN
        val shoulderShare = distance(points, 0, 1, width, height) / width
        if (shoulderShare < 0.12) return Hint.TOO_FAR
        if (shoulderShare > 0.7) return Hint.TOO_CLOSE
        return null
    }

    private fun visible(points: List<Landmark>, vararg indices: Int) =
        indices.all { points[it].visibility.toDouble() >= 0.5 }

    private fun distance(points: List<Landmark>, a: Int, b: Int, width: Int, height: Int) =
        hypot((points[a].x.toDouble() - points[b].x.toDouble()) * width,
            (points[a].y.toDouble() - points[b].y.toDouble()) * height)

    private fun gripErrors(grip: Double): Set<TechniqueError> = when {
        grip > 2.0 -> setOf(TechniqueError.HANDS_TOO_WIDE)
        grip < 1.1 -> setOf(TechniqueError.HANDS_TOO_NARROW)
        else -> emptySet()
    }

    private fun median(values: Collection<Double>) = values.sorted()[values.size / 2]

    private fun push(values: ArrayDeque<Double>, value: Double, limit: Int) {
        values.addLast(value)
        if (values.size > limit) values.removeFirst()
    }
}
