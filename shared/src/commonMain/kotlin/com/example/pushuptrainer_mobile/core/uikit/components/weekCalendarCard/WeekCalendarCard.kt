package com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.main_week_title
import pushuptrainermobile.shared.generated.resources.main_weekdays_short

private val DayCircleSize = 28.dp
private val WorkoutDotSize = 4.dp

/** Карточка «Твоя неделя»: диапазон дат и семь дней. */
@Composable
fun WeekCalendarCard(
    model: WeekCalendarUiModel,
    modifier: Modifier = Modifier,
) {
    val weekdays = stringArrayResource(Res.array.main_weekdays_short)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface)
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.main_week_title),
                style = AppTheme.typography.cardTitle,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = model.rangeText,
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            model.days.forEachIndexed { index, day ->
                WeekDay(
                    weekday = weekdays[index],
                    model = day,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WeekDay(
    weekday: String,
    model: WeekDayUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = weekday,
            style = AppTheme.typography.caption,
            color = colors.textSecondary,
        )
        Spacer(Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .size(DayCircleSize)
                .clip(CircleShape)
                .then(if (model.isToday) Modifier.background(colors.accent) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = model.dayOfMonth.toString(),
                style = AppTheme.typography.body,
                color = if (model.isToday) colors.onAccent else colors.textPrimary,
            )
        }
        Spacer(Modifier.height(4.dp))
        // Место под точку есть всегда, чтобы карточка не меняла высоту.
        Box(
            modifier = Modifier
                .size(WorkoutDotSize)
                .clip(CircleShape)
                .then(if (model.hasWorkout) Modifier.background(colors.accent) else Modifier),
        )
    }
}

@Preview
@Composable
private fun WeekCalendarCardLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        WeekCalendarCardPreviewContent()
    }
}

@Preview
@Composable
private fun WeekCalendarCardDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        WeekCalendarCardPreviewContent()
    }
}

@Composable
private fun WeekCalendarCardPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        WeekCalendarCard(
            model = WeekCalendarUiModel(
                rangeText = "19–25 октября",
                days = (19..25).map { day ->
                    WeekDayUiModel(
                        dayOfMonth = day,
                        isToday = day == 19,
                        hasWorkout = day == 19,
                    )
                },
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}
