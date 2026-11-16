package com.example.pushuptrainer_mobile.core.uikit.components.poseOverlay

import androidx.compose.runtime.Immutable

@Immutable
data class PosePointUiModel(val x: Float, val y: Float)

@Immutable
data class PoseSegmentUiModel(val start: PosePointUiModel, val end: PosePointUiModel)

@Immutable
data class PoseOverlayUiModel(
    val width: Int,
    val height: Int,
    val points: List<PosePointUiModel>,
    val segments: List<PoseSegmentUiModel>,
)

internal fun PosePointUiModel.inViewport(model: PoseOverlayUiModel, width: Float, height: Float): PosePointUiModel {
    val scale = maxOf(width / model.width, height / model.height)
    return PosePointUiModel(
        x * model.width * scale + (width - model.width * scale) / 2,
        y * model.height * scale + (height - model.height * scale) / 2,
    )
}
