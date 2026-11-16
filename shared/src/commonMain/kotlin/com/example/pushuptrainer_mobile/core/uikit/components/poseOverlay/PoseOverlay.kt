package com.example.pushuptrainer_mobile.core.uikit.components.poseOverlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme

@Composable
fun PoseOverlay(model: PoseOverlayUiModel, modifier: Modifier = Modifier) {
    Canvas(modifier.clipToBounds()) {
        fun PosePointUiModel.position(): Offset = inViewport(model, size.width, size.height).let { Offset(it.x, it.y) }
        model.segments.forEach {
            drawLine(Color.Black, it.start.position(), it.end.position(), 5.dp.toPx())
            drawLine(Color.Cyan, it.start.position(), it.end.position(), 3.dp.toPx())
        }
        model.points.forEach {
            drawCircle(Color.Black, 5.dp.toPx(), it.position())
            drawCircle(Color.Yellow, 3.dp.toPx(), it.position())
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
private fun PoseOverlayPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        val left = PosePointUiModel(0.3f, 0.3f)
        val right = PosePointUiModel(0.7f, 0.3f)
        PoseOverlay(
            PoseOverlayUiModel(360, 640, listOf(left, right), listOf(PoseSegmentUiModel(left, right))),
            Modifier.fillMaxSize().background(Color.DarkGray),
        )
    }
}
