package com.example.pushuptrainer_mobile.ml.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.poseOverlay.PoseOverlayUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.poseOverlay.PosePointUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.poseOverlay.PoseSegmentUiModel
import com.example.pushuptrainer_mobile.ml.domain.model.BodyPoint
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrame

internal fun PoseFrame.toOverlay(): PoseOverlayUiModel? {
    val pose = landmarks ?: return null
    if (imageWidth <= 0 || imageHeight <= 0 || pose.size != BodyPoint.entries.size) return null
    val points = pose.map { point ->
        point.takeIf { it.visibility >= 0.5f && it.x.isFinite() && it.y.isFinite() }
            ?.let { PosePointUiModel(1f - it.x, it.y) }
    }
    val segments = listOf(0 to 1, 0 to 2, 2 to 4, 1 to 3, 3 to 5, 0 to 6, 1 to 7, 6 to 7, 6 to 8, 8 to 10, 7 to 9, 9 to 11)
        .mapNotNull { (a, b) ->
            val start = points[a] ?: return@mapNotNull null
            val end = points[b] ?: return@mapNotNull null
            PoseSegmentUiModel(start, end)
        }
    return PoseOverlayUiModel(imageWidth, imageHeight, points.filterNotNull(), segments)
}
