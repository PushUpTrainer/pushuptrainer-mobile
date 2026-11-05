package com.example.pushuptrainer_mobile.core.uikit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.text.russianPluralString
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.img_streak_fire
import pushuptrainermobile.shared.generated.resources.streak_caption
import pushuptrainermobile.shared.generated.resources.streak_days_in_row_few
import pushuptrainermobile.shared.generated.resources.streak_days_in_row_many
import pushuptrainermobile.shared.generated.resources.streak_days_in_row_one

/** Данные карточки серии тренировок. */
data class StreakCardUiModel(
    val daysInRow: Int,
)

private const val HighlightDurationMillis = 360

/**
 * Карточка «N дней подряд / Серия тренировок».
 *
 * @param isHighlighted подсвеченное состояние: акцентный фон и цветной огонёк.
 * В неподсвеченном состоянии фон как у обычной карточки, а огонёк серый.
 * Переход между состояниями анимируется.
 */
@Composable
fun StreakCard(
    model: StreakCardUiModel,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = true,
) {
    val colors = AppTheme.colors
    val backgroundColor by animateColorAsState(
        targetValue = if (isHighlighted) colors.accentContainer else colors.surface,
        animationSpec = tween(HighlightDurationMillis, easing = EaseOut),
    )
    val fireSaturation by animateFloatAsState(
        targetValue = if (isHighlighted) 1f else 0f,
        animationSpec = tween(HighlightDurationMillis, easing = EaseOut),
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Res.drawable.img_streak_fire),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            colorFilter = ColorFilter.colorMatrix(
                ColorMatrix().apply { setToSaturation(fireSaturation) },
            ),
        )
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = russianPluralString(
                    count = model.daysInRow,
                    one = Res.string.streak_days_in_row_one,
                    few = Res.string.streak_days_in_row_few,
                    many = Res.string.streak_days_in_row_many,
                ),
                style = AppTheme.typography.cardTitleLarge,
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(Res.string.streak_caption),
                style = AppTheme.typography.caption,
                color = colors.textSecondary,
            )
        }
    }
}

@Preview
@Composable
private fun StreakCardLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        StreakCardPreviewContent()
    }
}

@Preview
@Composable
private fun StreakCardDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        StreakCardPreviewContent()
    }
}

@Composable
private fun StreakCardPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StreakCard(model = StreakCardUiModel(daysInRow = 3), isHighlighted = true)
            StreakCard(model = StreakCardUiModel(daysInRow = 3), isHighlighted = false)
            StreakCard(model = StreakCardUiModel(daysInRow = 1), isHighlighted = true)
        }
    }
}
