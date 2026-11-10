package com.example.pushuptrainer_mobile.core.uikit.components.poseIllustration

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.img_preparation_pose
import pushuptrainermobile.shared.generated.resources.preparation_pose_content_description

private const val POSE_ASPECT_RATIO = 320f / 208f

/** Иллюстрация правильной позы отжимания (ресурс из Figma). */
@Composable
fun PoseIllustration(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(Res.drawable.img_preparation_pose),
        contentDescription = stringResource(Res.string.preparation_pose_content_description),
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(POSE_ASPECT_RATIO),
        contentScale = ContentScale.FillWidth,
    )
}

@Preview
@Composable
private fun PoseIllustrationLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            PoseIllustration(modifier = Modifier.padding(20.dp))
        }
    }
}

@Preview
@Composable
private fun PoseIllustrationDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            PoseIllustration(modifier = Modifier.padding(20.dp))
        }
    }
}
