package com.example.pushuptrainer_mobile.core.uikit.components.poseIllustration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.preparation_pose_placeholder

/**
 * Иллюстрация правильной позы отжимания.
 *
 * TODO: заменить заглушку на реальный ресурс из Figma — экспортировать изображение
 * в `shared/src/commonMain/composeResources/drawable/img_preparation_pose.png`
 * и отрисовать его через `Image(painterResource(Res.drawable.img_preparation_pose))`
 * (по правилу skill: статичные изображения — это ресурсы, а не рисование на Canvas).
 */
@Composable
fun PoseIllustration(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.accentContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(Res.string.preparation_pose_placeholder),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
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
