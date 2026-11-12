package com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.text.russianPluralString
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.day_summary_trainings_few
import pushuptrainermobile.shared.generated.resources.day_summary_trainings_many
import pushuptrainermobile.shared.generated.resources.day_summary_trainings_one
import pushuptrainermobile.shared.generated.resources.ic_calendar
import pushuptrainermobile.shared.generated.resources.ic_repeat
import pushuptrainermobile.shared.generated.resources.main_today_repetitions_few
import pushuptrainermobile.shared.generated.resources.main_today_repetitions_many
import pushuptrainermobile.shared.generated.resources.main_today_repetitions_one

/** По макету второй показатель начинается в 158 dp от левого края содержимого. */
private val FirstStatWidth = 158.dp

/** Карточка итога дня: дата, тренировки и повторения в одну строку. */
@Composable
fun DaySummaryCard(
    model: DaySummaryUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface)
            .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 10.dp),
    ) {
        Text(
            text = model.dateText,
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
        )
        Spacer(Modifier.height(9.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            DayStat(
                icon = Res.drawable.ic_calendar,
                value = model.trainings,
                label = russianPluralString(
                    count = model.trainings,
                    one = Res.string.day_summary_trainings_one,
                    few = Res.string.day_summary_trainings_few,
                    many = Res.string.day_summary_trainings_many,
                ),
                modifier = Modifier.width(FirstStatWidth),
            )
            DayStat(
                icon = Res.drawable.ic_repeat,
                value = model.repetitions,
                label = russianPluralString(
                    count = model.repetitions,
                    one = Res.string.main_today_repetitions_one,
                    few = Res.string.main_today_repetitions_few,
                    many = Res.string.main_today_repetitions_many,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DayStat(
    icon: DrawableResource,
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = AppTheme.colors.accent,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = value.toString(),
            style = AppTheme.typography.statValue,
            color = AppTheme.colors.textPrimary,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            style = AppTheme.typography.captionSmall,
            color = AppTheme.colors.textSecondary,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun DaySummaryCardDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            DaySummaryCard(
                model = DaySummaryUiModel(dateText = "19 октября · Сегодня", trainings = 1, repetitions = 16),
                modifier = Modifier.padding(20.dp).width(320.dp),
            )
        }
    }
}

@Preview
@Composable
private fun DaySummaryCardLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            DaySummaryCard(
                model = DaySummaryUiModel(dateText = "19 октября · Сегодня", trainings = 3, repetitions = 52),
                modifier = Modifier.padding(20.dp).width(320.dp),
            )
        }
    }
}
