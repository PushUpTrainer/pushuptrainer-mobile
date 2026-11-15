package com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.ic_chevron_left
import pushuptrainermobile.shared.generated.resources.ic_chevron_right
import pushuptrainermobile.shared.generated.resources.main_next_week
import pushuptrainermobile.shared.generated.resources.main_previous_week
import pushuptrainermobile.shared.generated.resources.main_today_label
import pushuptrainermobile.shared.generated.resources.main_week_title
import pushuptrainermobile.shared.generated.resources.main_weekdays_short

private val DayCircleSize = 28.dp
private val WorkoutDotSize = 4.dp

/** Карточка «Твоя неделя»: диапазон дат и семь дней. */
@Composable
fun WeekCalendarCard(
    model: WeekCalendarUiModel,
    modifier: Modifier = Modifier,
    onDayClick: ((Long) -> Unit)? = null,
    onPreviousWeekClick: (() -> Unit)? = null,
    onNextWeekClick: (() -> Unit)? = null,
    onTodayClick: (() -> Unit)? = null,
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
            if (onTodayClick != null) {
                TextButton(onClick = onTodayClick) {
                    Text(stringResource(Res.string.main_today_label), color = AppTheme.colors.accent)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (onPreviousWeekClick != null) {
                IconButton(onClick = onPreviousWeekClick) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_chevron_left),
                        contentDescription = stringResource(Res.string.main_previous_week),
                        tint = AppTheme.colors.textPrimary,
                    )
                }
            }
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = model.rangeText,
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
            if (onNextWeekClick != null) {
                IconButton(onClick = onNextWeekClick, enabled = model.canGoForward) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_chevron_right),
                        contentDescription = stringResource(Res.string.main_next_week),
                        tint = AppTheme.colors.textSecondary.copy(alpha = if (model.canGoForward) 1f else 0.3f),
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            model.days.forEachIndexed { index, day ->
                WeekDay(
                    weekday = weekdays[index],
                    model = day,
                    onClick = onDayClick?.let { { it(day.epochDay) } },
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
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors

    Column(
        modifier = modifier.then(
            if (onClick != null) Modifier
                .semantics { contentDescription = model.dateLabel }
                .selectable(selected = model.isSelected, enabled = model.enabled, role = Role.Button, onClick = onClick)
            else Modifier,
        ).padding(vertical = 4.dp),
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
                .then(
                    when {
                        model.isSelected -> Modifier.background(colors.accent)
                        model.isToday -> Modifier.border(1.dp, colors.accent, CircleShape)
                        else -> Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = model.dayOfMonth.toString(),
                style = AppTheme.typography.body,
                color = if (model.isSelected) colors.onAccent else colors.textPrimary.copy(alpha = if (model.enabled) 1f else 0.35f),
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
