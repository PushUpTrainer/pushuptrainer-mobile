package com.example.pushuptrainer_mobile.features.progress.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthCalendarCard
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeader
import com.example.pushuptrainer_mobile.core.uikit.components.screenHeader.ScreenHeaderUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.statTile.StatTile
import com.example.pushuptrainer_mobile.core.uikit.components.statTile.StatTileIconUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.statTile.StatTileUiModel
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import com.example.pushuptrainer_mobile.features.progress.domain.model.ProgressHistory
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.bottom_bar_progress
import pushuptrainermobile.shared.generated.resources.main_history_unavailable
import pushuptrainermobile.shared.generated.resources.main_retry
import pushuptrainermobile.shared.generated.resources.progress_subtitle
import pushuptrainermobile.shared.generated.resources.progress_today
import pushuptrainermobile.shared.generated.resources.progress_total
import pushuptrainermobile.shared.generated.resources.progress_record

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
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextButton(onClick = { onEvent(ProgressUiEvent.TodayClicked) }, modifier = Modifier.align(Alignment.End)) {
                    Text(stringResource(Res.string.progress_today), color = AppTheme.colors.accent)
                }
                MonthCalendarCard(
                    model = state.calendar,
                    onPreviousMonthClick = { onEvent(ProgressUiEvent.PreviousMonthClicked) },
                    onNextMonthClick = { onEvent(ProgressUiEvent.NextMonthClicked) },
                    onDayClick = { onEvent(ProgressUiEvent.DaySelected(it)) },
                )
                DaySummaryCard(state.day)
                StatTile(StatTileUiModel(StatTileIconUiModel.Repetitions, state.totalRepetitions, stringResource(Res.string.progress_total)), Modifier.fillMaxWidth())
                StatTile(StatTileUiModel(StatTileIconUiModel.Record, state.bestWorkout, stringResource(Res.string.progress_record)), Modifier.fillMaxWidth())
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

private fun progressPreview(): ProgressUiState {
    val day = LocalDate(2026, 11, 16)
    return ProgressHistory(day, day, setOf(day), 2, 35, 180, 25).toUiState()
}
