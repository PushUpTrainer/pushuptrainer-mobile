package com.example.pushuptrainer_mobile.features.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard.DaySummaryCard
import com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard.DaySummaryUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthCalendarCard
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthCalendarUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthDayPeriodUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard.MonthDayUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.statTile.StatTile
import com.example.pushuptrainer_mobile.core.uikit.components.statTile.StatTileIconUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.statTile.StatTileUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.themeToggleButton.ThemeToggleButton
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.onboarding_appearance_dark
import pushuptrainermobile.shared.generated.resources.onboarding_appearance_hint
import pushuptrainermobile.shared.generated.resources.onboarding_appearance_light
import pushuptrainermobile.shared.generated.resources.onboarding_appearance_screen_subtitle
import pushuptrainermobile.shared.generated.resources.onboarding_appearance_screen_title
import pushuptrainermobile.shared.generated.resources.onboarding_camera_caption
import pushuptrainermobile.shared.generated.resources.onboarding_progress_day
import pushuptrainermobile.shared.generated.resources.onboarding_progress_month
import pushuptrainermobile.shared.generated.resources.onboarding_stat_record
import pushuptrainermobile.shared.generated.resources.onboarding_stat_total_repetitions

/*
 * Иллюстрации шагов онбординга. Это неизменный пример интерфейса приложения,
 * поэтому данные в них демонстрационные и не зависят от реальных тренировок.
 */

/**
 * Шаг 1. Место под пример экрана подготовки.
 *
 * TODO: после слияния ветки экрана подготовки заменить на PreparationModeSwitch
 *  и GoalSelectorCard из core/uikit (макет *4.1).
 */
@Composable
internal fun ModesExample(modifier: Modifier = Modifier) {
    ExamplePlaceholder(height = 286, modifier = modifier)
}

/**
 * Шаг 2. Иллюстрация позы и подпись.
 *
 * TODO: после слияния ветки экрана подготовки заменить заглушку на PoseIllustration (макет *4.2).
 */
@Composable
internal fun PhonePlacementExample(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ExamplePlaceholder(height = 208)
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.onboarding_camera_caption),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

/** Шаг 3. Календарь месяца и итог дня (макет *4.3). */
@Composable
internal fun ProgressExample(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        MonthCalendarCard(
            model = exampleMonth(title = stringResource(Res.string.onboarding_progress_month)),
            onPreviousMonthClick = {},
            onNextMonthClick = {},
        )
        Spacer(Modifier.height(16.dp))
        DaySummaryCard(
            model = DaySummaryUiModel(
                dateText = stringResource(Res.string.onboarding_progress_day),
                trainings = 1,
                repetitions = 16,
            ),
        )
    }
}

/** Шаг 4. Шапка главного экрана и одни и те же плитки в тёмной и светлой теме (макет *4.4). */
@Composable
internal fun AppearanceExample(modifier: Modifier = Modifier) {
    val repetitions = StatTileUiModel(
        icon = StatTileIconUiModel.Repetitions,
        value = "86",
        caption = stringResource(Res.string.onboarding_stat_total_repetitions),
    )
    val record = StatTileUiModel(
        icon = StatTileIconUiModel.Record,
        value = "16",
        caption = stringResource(Res.string.onboarding_stat_record),
    )
    val subtitleStyle = AppTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = stringResource(Res.string.onboarding_appearance_screen_title),
                style = AppTheme.typography.screenTitle,
                color = AppTheme.colors.textPrimary,
                modifier = Modifier.weight(1f).padding(top = 4.dp),
            )
            ThemeToggleButton(isDarkTheme = AppTheme.isDarkTheme, onClick = {})
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.onboarding_appearance_screen_subtitle),
            style = subtitleStyle,
            color = AppTheme.colors.textSecondary,
        )
        Spacer(Modifier.height(42.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ThemeColumn(
                label = stringResource(Res.string.onboarding_appearance_dark),
                darkTheme = true,
                tiles = listOf(repetitions, record),
                modifier = Modifier.weight(1f),
            )
            ThemeColumn(
                label = stringResource(Res.string.onboarding_appearance_light),
                darkTheme = false,
                tiles = listOf(repetitions, record),
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(Res.string.onboarding_appearance_hint),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Колонка плиток, нарисованная в заданной теме независимо от текущей. */
@Composable
private fun ThemeColumn(
    label: String,
    darkTheme: Boolean,
    tiles: List<StatTileUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = AppTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = AppTheme.colors.textSecondary,
        )
        Spacer(Modifier.height(16.dp))
        PushUpTrainerTheme(darkTheme = darkTheme) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                tiles.forEach { tile ->
                    StatTile(model = tile, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun ExamplePlaceholder(
    height: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface),
    )
}

/** Демонстрационный октябрь 2026: сегодня 19-е, отмечены дни с тренировками. */
private fun exampleMonth(title: String): MonthCalendarUiModel {
    val today = 19
    val workoutDays = setOf(12, 14, 15, 17, 18, 19)
    val leadingEmptyDays = 3 // 1 октября 2026 — четверг
    val cells = List(leadingEmptyDays) { null } + (1..31).map { day ->
        MonthDayUiModel(
            dayOfMonth = day,
            period = when {
                day < today -> MonthDayPeriodUiModel.Past
                day == today -> MonthDayPeriodUiModel.Today
                else -> MonthDayPeriodUiModel.Future
            },
            hasWorkout = day in workoutDays,
        )
    }
    return MonthCalendarUiModel(
        title = title,
        weeks = cells.chunked(7).map { week -> week + List(7 - week.size) { null } },
    )
}
