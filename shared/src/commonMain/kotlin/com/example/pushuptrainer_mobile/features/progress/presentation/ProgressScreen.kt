package com.example.pushuptrainer_mobile.features.progress.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard.DaySummaryCard
import com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard.DaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard.EmptyDayUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.emptyProgressCard.EmptyProgressCard
import com.example.pushuptrainer_mobile.core.uikit.components.emptyProgressCard.EmptyProgressUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthCalendarCard
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthCalendarUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthDayPeriodUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthDayUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeader
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeaderUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.statTile.StatTile
import com.example.pushuptrainer_mobile.core.uikit.components.statTile.StatTileIconUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.statTile.StatTileUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCard
import com.example.pushuptrainer_mobile.core.uikit.components.streakCard.StreakCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.bottom_bar_progress
import pushuptrainermobile.shared.generated.resources.main_history_unavailable
import pushuptrainermobile.shared.generated.resources.main_prepare
import pushuptrainermobile.shared.generated.resources.main_retry
import pushuptrainermobile.shared.generated.resources.progress_all_time
import pushuptrainermobile.shared.generated.resources.progress_average
import pushuptrainermobile.shared.generated.resources.progress_empty_description
import pushuptrainermobile.shared.generated.resources.progress_empty_title
import pushuptrainermobile.shared.generated.resources.progress_record
import pushuptrainermobile.shared.generated.resources.progress_subtitle
import pushuptrainermobile.shared.generated.resources.progress_total
import pushuptrainermobile.shared.generated.resources.progress_trainings

@Composable
internal fun ProgressScreen(state: ProgressUiState, onEvent: (ProgressUiEvent) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().background(AppTheme.colors.background).padding(horizontal = 20.dp)) {
        ScreenHeader(
            ScreenHeaderUiModel(stringResource(Res.string.bottom_bar_progress), stringResource(Res.string.progress_subtitle)),
            onThemeToggleClick = { onEvent(ProgressUiEvent.ThemeToggleClicked) },
        )
        when (state) {
            ProgressUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppTheme.colors.accent)
            }
            ProgressUiState.Unavailable -> Column(Modifier.fillMaxWidth().padding(top = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(Res.string.main_history_unavailable), color = AppTheme.colors.textSecondary)
                TextButton(onClick = { onEvent(ProgressUiEvent.RetryClicked) }) {
                    Text(stringResource(Res.string.main_retry), color = AppTheme.colors.accent)
                }
            }
            is ProgressUiState.Content -> Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(top = 20.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StreakCard(state.streak, isHighlighted = state.isStreakHighlighted)
                MonthCalendarCard(
                    model = state.calendar,
                    onPreviousMonthClick = { onEvent(ProgressUiEvent.PreviousMonthClicked) },
                    onNextMonthClick = { onEvent(ProgressUiEvent.NextMonthClicked) },
                    onDayClick = { onEvent(ProgressUiEvent.DaySelected(it)) },
                )
                if (state.isHistoryEmpty) {
                    EmptyProgressCard(
                        model = EmptyProgressUiModel(
                            title = stringResource(Res.string.progress_empty_title),
                            description = stringResource(Res.string.progress_empty_description),
                            actionLabel = stringResource(Res.string.main_prepare),
                        ),
                        onPrepareClick = { onEvent(ProgressUiEvent.PrepareClicked) },
                    )
                } else {
                    DaySummaryCard(state.day)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(Res.string.progress_all_time), style = AppTheme.typography.cardTitle, color = AppTheme.colors.textPrimary)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatTile(StatTileUiModel(StatTileIconUiModel.Repetitions, state.totalRepetitions, stringResource(Res.string.progress_total)), Modifier.weight(1f))
                                StatTile(StatTileUiModel(StatTileIconUiModel.Trainings, state.totalWorkouts, stringResource(Res.string.progress_trainings)), Modifier.weight(1f))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                StatTile(StatTileUiModel(StatTileIconUiModel.Record, state.bestWorkout, stringResource(Res.string.progress_record)), Modifier.weight(1f))
                                StatTile(StatTileUiModel(StatTileIconUiModel.Average, state.averageRepetitions, stringResource(Res.string.progress_average)), Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 360, heightDp = 760)
@Composable
private fun ProgressLightPreview() {
    PushUpTrainerTheme(darkTheme = false) { ProgressScreen(progressPreview(), {}) }
}

@Preview(widthDp = 360, heightDp = 760)
@Composable
private fun ProgressDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) { ProgressScreen(progressPreview(), {}) }
}

@Preview(widthDp = 360, heightDp = 760)
@Composable
private fun ProgressEmptyDayPreview() {
    ProgressEmptyDayPreviewContent(true)
}

@Preview(widthDp = 360, heightDp = 760)
@Composable
private fun ProgressEmptyDayLightPreview() {
    ProgressEmptyDayPreviewContent(false)
}

@Composable
private fun ProgressEmptyDayPreviewContent(darkTheme: Boolean) {
    PushUpTrainerTheme(darkTheme = darkTheme) {
        ProgressScreen(progressPreview().copy(
            day = DaySummaryUiModel("19 октября · Сегодня", 0, 0, EmptyDayUiModel.Today),
            isStreakHighlighted = false,
        ), {})
    }
}

@Preview(widthDp = 360, heightDp = 760)
@Composable
private fun ProgressEmptyHistoryPreview() {
    ProgressEmptyHistoryPreviewContent(false)
}

@Preview(widthDp = 360, heightDp = 760)
@Composable
private fun ProgressEmptyHistoryDarkPreview() {
    ProgressEmptyHistoryPreviewContent(true)
}

@Composable
private fun ProgressEmptyHistoryPreviewContent(darkTheme: Boolean) {
    PushUpTrainerTheme(darkTheme = darkTheme) {
        ProgressScreen(progressPreview().copy(
            isHistoryEmpty = true,
            streak = StreakCardUiModel(0, showStartPrompt = true),
            isStreakHighlighted = false,
            calendar = progressPreview().calendar.copy(weeks = progressPreview().calendar.weeks.map { week ->
                week.map { it?.copy(hasWorkout = false) }
            }),
        ), {})
    }
}

private fun progressPreview() = ProgressUiState.Content(
    calendar = MonthCalendarUiModel(
        title = "Октябрь 2026",
        weeks = (List<MonthDayUiModel?>(3) { null } + (1..31).map { day ->
            MonthDayUiModel(
                dayOfMonth = day,
                period = when {
                    day < 19 -> MonthDayPeriodUiModel.Past
                    day == 19 -> MonthDayPeriodUiModel.Today
                    else -> MonthDayPeriodUiModel.Future
                },
                hasWorkout = day in 17..19,
                isSelected = day == 19,
            )
        }).chunked(7).map { it + List(7 - it.size) { null } },
        canGoForward = false,
    ),
    day = DaySummaryUiModel("19 октября · Сегодня", 1, 16),
    totalRepetitions = "86",
    bestWorkout = "16",
    totalWorkouts = "6",
    averageRepetitions = "14,3",
    streak = StreakCardUiModel(3),
    isStreakHighlighted = true,
    isHistoryEmpty = false,
    selectedEpochDay = 0,
    todayEpochDay = 0,
)
