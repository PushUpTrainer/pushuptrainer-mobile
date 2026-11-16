package com.example.pushuptrainer_mobile.features.workout

import com.example.pushuptrainer_mobile.features.workout.domain.model.WorkoutPhase
import com.example.pushuptrainer_mobile.features.workout.domain.usecase.WorkoutSessionUseCase
import com.example.pushuptrainer_mobile.ml.domain.model.Hint
import com.example.pushuptrainer_mobile.ml.domain.model.Phase
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrame
import com.example.pushuptrainer_mobile.ml.domain.model.PushupAnalyzer
import com.example.pushuptrainer_mobile.ml.domain.model.PushupState
import com.example.pushuptrainer_mobile.ml.domain.model.TechniqueError
import com.example.pushuptrainer_mobile.ml.domain.model.WorkoutSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TestTimeSource
import kotlin.time.Duration.Companion.seconds

class WorkoutSessionTest {
    @Test
    fun preparationIgnoresFramesErrorsAndDurationUntilFiveSeconds() {
        for (goal in listOf(null, 1)) {
            val clock = TestTimeSource()
            val session = WorkoutSessionUseCase(goal, { CountingAnalyzer() }, timeSource = clock)
            session.ready(0)
            assertTrue(session.onFrame(0, frame(1)))
            clock += 4999.milliseconds
            assertEquals(WorkoutPhase.Running, session.state.value.phase)
            session.ready(0)
            assertTrue(session.onFrame(0, frame(2)))
            val preparing = session.snapshot("test", goal)
            assertEquals(0, preparing.repetitions)
            assertEquals(0L, preparing.durationMs)
            assertTrue(preparing.errors.isEmpty())
            clock += 1.milliseconds
            assertEquals(WorkoutPhase.Running, session.state.value.phase)
            assertFalse(session.onFrame(0, frame(2)))
            session.onFrame(0, frame(3))
            assertEquals(1, session.state.value.analysis?.reps)
        }
    }

    @Test
    fun pausedOrFailedSessionCannotCountAndResumeGetsFullCooldown() {
        val clock = TestTimeSource()
        val session = WorkoutSessionUseCase(analyzerFactory = { CountingAnalyzer() }, timeSource = clock)
        session.ready(0)
        clock += 3.seconds
        session.pause()
        clock += 10.seconds
        assertEquals(WorkoutPhase.Paused, session.state.value.phase)
        session.resume()
        session.ready(1)
        session.onFrame(1, frame(2))
        assertEquals(0, session.snapshot("resumed", null).repetitions)
        session.cameraFailed(1)
        clock += 5.seconds
        assertEquals(WorkoutPhase.CameraError, session.state.value.phase)
        session.resume()
        session.ready(2)
        session.onFrame(2, frame(1))
        val result = session.finish("empty", null)
        clock += 5.seconds
        assertEquals(WorkoutPhase.Finished, session.state.value.phase)
        assertEquals(0, result.repetitions)
        assertEquals(0L, result.durationMs)
    }

    @Test
    fun pausePreservesCompletedRepsAndExcludesBackgroundDuration() {
        val clock = TestTimeSource()
        val session = WorkoutSessionUseCase(analyzerFactory = { CountingAnalyzer() }, nowMillis = { 1234L }, timeSource = clock)
        session.ready(0)
        clock += 5.seconds
        session.onFrame(0, frame(1))
        clock += 3.seconds
        session.pause()
        clock += 20.seconds
        session.onFrame(0, frame(2))
        session.resume()
        session.ready(0)
        assertEquals(WorkoutPhase.Starting, session.state.value.phase)
        session.ready(1)
        clock += 5.seconds
        session.onFrame(0, frame(3))
        session.onFrame(1, frame(1))
        session.onFrame(1, frame(1))
        clock += 2.seconds
        val result = session.finish("one", 10)
        assertEquals(2, result.repetitions)
        assertEquals(5000L, result.durationMs)
        assertEquals(1234L, result.startedAtEpochMs)
        assertEquals(mapOf("HANDS_TOO_WIDE" to 2), result.errors)
        session.onFrame(1, frame(4))
        assertEquals(result, session.finish("one", 10))
    }

    @Test
    fun goalFreezesAtTargetBeforeAnyLateFrame() {
        val clock = TestTimeSource()
        val session = WorkoutSessionUseCase(goal = 2, analyzerFactory = { CountingAnalyzer() }, timeSource = clock)
        session.ready(0)
        clock += 5.seconds
        repeat(10) { session.onFrame(0, frame(it.toLong())) }
        assertEquals(WorkoutPhase.Finished, session.state.value.phase)
        assertEquals(2, session.finish("one", 2).repetitions)
    }

    @Test
    fun realCounterReportsMissingPersonWithoutCounting() {
        val clock = TestTimeSource()
        val session = WorkoutSessionUseCase(timeSource = clock)
        session.ready(0)
        clock += 5.seconds
        session.onFrame(0, frame(1))
        assertEquals(Hint.NO_PERSON, session.state.value.analysis?.hint)
        assertEquals(0, session.finish("missing", null).repetitions)
    }

    @Test
    fun cameraFailureCanRetryAndOldCallbacksCannotAffectNewAttempt() {
        val clock = TestTimeSource()
        val session = WorkoutSessionUseCase(analyzerFactory = { CountingAnalyzer() }, timeSource = clock)
        session.cameraFailed(0)
        assertEquals(WorkoutPhase.CameraError, session.state.value.phase)
        session.resume()
        session.ready(1)
        clock += 5.seconds
        session.cameraFailed(0)
        session.onFrame(0, frame(1))
        assertNull(session.state.value.analysis)
        session.onFrame(1, frame(1))
        assertEquals(WorkoutPhase.Running, session.state.value.phase)
        assertEquals(1, session.state.value.analysis?.reps)
    }
}

internal fun frame(timestamp: Long) = PoseFrame(timestamp, 640, 480, null)

internal class CountingAnalyzer : PushupAnalyzer {
    private var count = 0
    override fun onFrame(frame: PoseFrame) = PushupState(++count, Phase.UP, null, emptySet())
    override fun finish() = WorkoutSummary(count, 0, if (count == 0) emptyMap() else mapOf(TechniqueError.HANDS_TOO_WIDE to count))
}
