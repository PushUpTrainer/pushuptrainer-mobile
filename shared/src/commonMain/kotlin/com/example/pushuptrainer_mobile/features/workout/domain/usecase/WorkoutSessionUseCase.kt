package com.example.pushuptrainer_mobile.features.workout.domain.usecase

import com.example.pushuptrainer_mobile.features.workout.domain.model.CompletedWorkout
import com.example.pushuptrainer_mobile.features.workout.domain.model.WorkoutPhase
import com.example.pushuptrainer_mobile.features.workout.domain.model.WorkoutSessionState
import com.example.pushuptrainer_mobile.ml.domain.analysis.PushupCounter
import com.example.pushuptrainer_mobile.ml.domain.model.Phase
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrame
import com.example.pushuptrainer_mobile.ml.domain.model.PushupAnalyzer
import com.example.pushuptrainer_mobile.ml.domain.model.PushupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import kotlin.time.TimeSource
import kotlin.time.Duration.Companion.seconds

internal class WorkoutSessionUseCase(
    private val goal: Int? = null,
    private val analyzerFactory: () -> PushupAnalyzer = { PushupCounter() },
    private val nowMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() },
    private val timeSource: TimeSource = TimeSource.Monotonic,
) {
    private val _state = MutableStateFlow(WorkoutSessionState())
    val state = _state.asStateFlow()
    private var analyzer = analyzerFactory()
    private var startedAt = nowMillis()
    private var activeSince: kotlin.time.TimeMark? = null
    private var durationMs = 0L
    private var repetitions = 0
    private var errors = emptyMap<String, Int>()
    private var lastTimestamp: Long? = null

    fun restore(workout: CompletedWorkout, finished: Boolean) {
        startedAt = workout.startedAtEpochMs
        durationMs = workout.durationMs
        repetitions = workout.repetitions
        errors = workout.errors
        _state.value = WorkoutSessionState(
            phase = if (finished) WorkoutPhase.Finished else WorkoutPhase.Paused,
            analysis = PushupState(repetitions, Phase.UP, null, emptySet()),
        )
    }

    fun ready(generation: Int) {
        if (_state.value.generation != generation || _state.value.phase != WorkoutPhase.Starting) return
        activeSince = timeSource.markNow() + COUNTER_START_DELAY_SECONDS.seconds
        _state.value = _state.value.copy(phase = WorkoutPhase.Running)
    }

    fun onFrame(generation: Int, frame: PoseFrame): Boolean {
        if (_state.value.generation != generation || _state.value.phase != WorkoutPhase.Running) return false
        if (lastTimestamp?.let { frame.timestampMs <= it } == true) return false
        lastTimestamp = frame.timestampMs
        if (activeSince?.hasPassedNow() != true) return true
        val result = analyzer.onFrame(frame)
        val total = repetitions + result.reps
        _state.value = _state.value.copy(analysis = result.copy(reps = total))
        if (goal != null && total >= goal) {
            stopSegment()
            _state.value = _state.value.copy(phase = WorkoutPhase.Finished)
        }
        return true
    }

    fun cameraFailed(generation: Int) {
        if (_state.value.generation != generation) return
        if (_state.value.phase != WorkoutPhase.Starting && _state.value.phase != WorkoutPhase.Running) return
        stopSegment()
        _state.value = _state.value.copy(phase = WorkoutPhase.CameraError)
    }

    fun pause() {
        if (_state.value.phase != WorkoutPhase.Starting && _state.value.phase != WorkoutPhase.Running) return
        stopSegment()
        _state.value = _state.value.copy(phase = WorkoutPhase.Paused)
    }

    fun resume() {
        if (_state.value.phase != WorkoutPhase.Paused && _state.value.phase != WorkoutPhase.CameraError) return
        _state.value = _state.value.copy(
            phase = WorkoutPhase.Starting,
            generation = _state.value.generation + 1,
        )
    }

    fun finish(id: String, goal: Int?): CompletedWorkout {
        if (_state.value.phase != WorkoutPhase.Finished) {
            stopSegment()
            _state.value = _state.value.copy(phase = WorkoutPhase.Finished)
        }
        return snapshot(id, goal)
    }

    fun snapshot(id: String, goal: Int?): CompletedWorkout {
        val summary = analyzer.finish()
        return CompletedWorkout(
            id = id,
            startedAtEpochMs = startedAt,
            durationMs = durationMs + (activeSince?.elapsedNow()?.inWholeMilliseconds?.coerceAtLeast(0L) ?: 0L),
            repetitions = repetitions + summary.reps,
            goal = goal,
            errors = mergeErrors(summary.errorCounts.mapKeys { it.key.name }),
        )
    }

    private fun stopSegment() {
        val summary = analyzer.finish()
        repetitions += summary.reps
        errors = mergeErrors(summary.errorCounts.mapKeys { it.key.name })
        durationMs += activeSince?.elapsedNow()?.inWholeMilliseconds?.coerceAtLeast(0L) ?: 0L
        activeSince = null
        analyzer = analyzerFactory()
        lastTimestamp = null
    }

    private fun mergeErrors(current: Map<String, Int>): Map<String, Int> =
        (errors.keys + current.keys).associateWith { (errors[it] ?: 0) + (current[it] ?: 0) }

    private companion object {
        const val COUNTER_START_DELAY_SECONDS = 5
    }
}
