package com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
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
import pushuptrainermobile.shared.generated.resources.main_weekdays_short
import pushuptrainermobile.shared.generated.resources.month_calendar_next
import pushuptrainermobile.shared.generated.resources.month_calendar_previous

private val CellSize = 32.dp
private val TodayCircleSize = 24.dp
private val WorkoutDotSize = 4.dp

/** Календарь месяца: стрелки, дни недели и сетка чисел с отметками тренировок. */
@Composable
fun MonthCalendarCard(
    model: MonthCalendarUiModel,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface)
            .padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MonthArrow(
                painter = painterResource(Res.drawable.ic_chevron_left),
                contentDescription = stringResource(Res.string.month_calendar_previous),
                onClick = onPreviousMonthClick,
            )
            Text(
                text = model.title,
                style = AppTheme.typography.cardTitle,
                color = AppTheme.colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            MonthArrow(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = stringResource(Res.string.month_calendar_next),
                onClick = onNextMonthClick,
            )
        }
        Spacer(Modifier.height(8.dp))
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            WeekdayRow()
            Spacer(Modifier.height(8.dp))
            model.weeks.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    week.forEach { day -> MonthDayCell(day) }
                }
            }
        }
    }
}

@Composable
private fun MonthArrow(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = AppTheme.colors.textSecondary,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun WeekdayRow() {
    val weekdays = stringArrayResource(Res.array.main_weekdays_short)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        weekdays.forEach { weekday ->
            Text(
                text = weekday,
                style = AppTheme.typography.navLabel,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(CellSize),
            )
        }
    }
}

@Composable
private fun MonthDayCell(day: MonthDayUiModel?) {
    Box(
        modifier = Modifier.size(CellSize),
        contentAlignment = Alignment.TopCenter,
    ) {
        if (day == null) return@Box
        val colors = AppTheme.colors
        if (day.period == MonthDayPeriodUiModel.Today) {
            Box(
                modifier = Modifier
                    .size(TodayCircleSize)
                    .clip(CircleShape)
                    .background(colors.accent),
            )
        }
        Text(
            text = day.dayOfMonth.toString(),
            style = AppTheme.typography.labelMedium,
            color = when (day.period) {
                MonthDayPeriodUiModel.Past -> colors.textPrimary
                MonthDayPeriodUiModel.Today -> colors.onAccent
                MonthDayPeriodUiModel.Future -> colors.textSecondary
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp),
        )
        if (day.hasWorkout) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
                    .size(WorkoutDotSize)
                    .clip(CircleShape)
                    .background(colors.accent),
            )
        }
    }
}

/** Тестовый месяц для Preview: октябрь 2026, сегодня 19-е. */
private fun previewMonthCalendar(): MonthCalendarUiModel {
    val workoutDays = setOf(12, 14, 15, 17, 18, 19)
    val leadingEmptyDays = 3 // 1 октября 2026 — четверг
    val cells = List(leadingEmptyDays) { null } + (1..31).map { day ->
        MonthDayUiModel(
            dayOfMonth = day,
            period = when {
                day < 19 -> MonthDayPeriodUiModel.Past
                day == 19 -> MonthDayPeriodUiModel.Today
                else -> MonthDayPeriodUiModel.Future
            },
            hasWorkout = day in workoutDays,
        )
    }
    val weeks = cells.chunked(7).map { week -> week + List(7 - week.size) { null } }
    return MonthCalendarUiModel(title = "Октябрь 2026", weeks = weeks)
}

@Preview
@Composable
private fun MonthCalendarCardDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            MonthCalendarCard(
                model = previewMonthCalendar(),
                onPreviousMonthClick = {},
                onNextMonthClick = {},
                modifier = Modifier.padding(20.dp).width(320.dp),
            )
        }
    }
}

@Preview
@Composable
private fun MonthCalendarCardLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            MonthCalendarCard(
                model = previewMonthCalendar(),
                onPreviousMonthClick = {},
                onNextMonthClick = {},
                modifier = Modifier.padding(20.dp).width(320.dp),
            )
        }
    }
}
