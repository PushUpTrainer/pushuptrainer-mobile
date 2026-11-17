package com.example.pushuptrainer_mobile.ml.domain.model

class PoseFrameGate(readyAtNanos: Long) {
    private val deadlineNanos = readyAtNanos + PREPARATION_DURATION_MS * 1_000_000L

    fun allows(capturedAtNanos: Long, nowNanos: Long): Boolean =
        nowNanos >= deadlineNanos && capturedAtNanos >= deadlineNanos && capturedAtNanos <= nowNanos

    companion object {
        const val PREPARATION_DURATION_MS = 5_000L
    }
}
