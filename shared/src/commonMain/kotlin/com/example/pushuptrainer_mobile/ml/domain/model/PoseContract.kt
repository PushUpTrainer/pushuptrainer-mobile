package com.example.pushuptrainer_mobile.ml.domain.model

data class Landmark(
    val x: Float,
    val y: Float,
    val z: Float,
    val visibility: Float,
    val presence: Float,
)

enum class BodyPoint(val mediaPipeIndex: Int) {
    LEFT_SHOULDER(11), RIGHT_SHOULDER(12),
    LEFT_ELBOW(13), RIGHT_ELBOW(14),
    LEFT_WRIST(15), RIGHT_WRIST(16),
    LEFT_HIP(23), RIGHT_HIP(24),
    LEFT_KNEE(25), RIGHT_KNEE(26),
    LEFT_ANKLE(27), RIGHT_ANKLE(28),
}

data class PoseFrame(
    val timestampMs: Long,
    val imageWidth: Int,
    val imageHeight: Int,
    val landmarks: List<Landmark>?,
)

fun interface PoseListener { fun onFrame(frame: PoseFrame) }
interface PoseSource {
    fun start(listener: PoseListener)
    fun stop()
}
enum class Phase { UP, DOWN }
enum class Hint { NO_PERSON, ARMS_HIDDEN, TOO_FAR, TOO_CLOSE }
enum class TechniqueError { SHALLOW_REP, HANDS_TOO_WIDE, HANDS_TOO_NARROW }

data class PushupState(
    val reps: Int,
    val phase: Phase,
    val hint: Hint?,
    val errors: Set<TechniqueError>,
)

data class WorkoutSummary(
    val reps: Int,
    val durationMs: Long,
    val errorCounts: Map<TechniqueError, Int>,
)

interface PushupAnalyzer {
    fun onFrame(frame: PoseFrame): PushupState
    fun finish(): WorkoutSummary
}
