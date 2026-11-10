package com.example.pushuptrainer_mobile.core.uikit.components.todaySummaryCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.text.russianPluralString
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.ic_repeat
import pushuptrainermobile.shared.generated.resources.main_today_empty_hint
import pushuptrainermobile.shared.generated.resources.main_today_empty_title
import pushuptrainermobile.shared.generated.resources.main_today_label
import pushuptrainermobile.shared.generated.resources.main_today_repetitions_few
import pushuptrainermobile.shared.generated.resources.main_today_repetitions_many
import pushuptrainermobile.shared.generated.resources.main_today_repetitions_one
import pushuptrainermobile.shared.generated.resources.main_today_trainings_few
import pushuptrainermobile.shared.generated.resources.main_today_trainings_many
import pushuptrainermobile.shared.generated.resources.main_today_trainings_one

/** Карточка «Сегодня»: пустое состояние или итог за день. */
@Composable
fun TodaySummaryCard(
    model: TodaySummaryUiModel,
    modifier: Modifier = Modifier,
    showPreparationHint: Boolean = true,
) {
    val cardModifier = modifier
        .fillMaxWidth()
        .heightIn(min = 136.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(AppTheme.colors.surface)
        .padding(16.dp)

    when (model) {
        TodaySummaryUiModel.Empty -> TodayEmptyContent(showPreparationHint = showPreparationHint, modifier = cardModifier)
        is TodaySummaryUiModel.Done -> TodayDoneContent(model = model, modifier = cardModifier)
    }
}

@Composable
private fun TodayEmptyContent(showPreparationHint: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.main_today_label),
            style = AppTheme.typography.caption,
            color = AppTheme.colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.main_today_empty_title),
            style = AppTheme.typography.cardTitle,
            color = AppTheme.colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        if (showPreparationHint) {
            Text(
                text = stringResource(Res.string.main_today_empty_hint),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun TodayDoneContent(
    model: TodaySummaryUiModel.Done,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_repeat),
            contentDescription = null,
            tint = AppTheme.colors.accent,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(Res.string.main_today_label),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
            Text(
                text = model.repetitions.toString(),
                style = AppTheme.typography.screenTitle,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                text = russianPluralString(
                    count = model.repetitions,
                    one = Res.string.main_today_repetitions_one,
                    few = Res.string.main_today_repetitions_few,
                    many = Res.string.main_today_repetitions_many,
                ),
                style = AppTheme.typography.body,
                color = AppTheme.colors.textPrimary,
            )
            Text(
                text = russianPluralString(
                    count = model.trainings,
                    one = Res.string.main_today_trainings_one,
                    few = Res.string.main_today_trainings_few,
                    many = Res.string.main_today_trainings_many,
                ),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

@Preview
@Composable
private fun TodaySummaryCardLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        TodaySummaryCardPreviewContent()
    }
}

@Preview
@Composable
private fun TodaySummaryCardDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        TodaySummaryCardPreviewContent()
    }
}

@Composable
private fun TodaySummaryCardPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TodaySummaryCard(model = TodaySummaryUiModel.Empty)
            TodaySummaryCard(model = TodaySummaryUiModel.Done(repetitions = 16, trainings = 1))
        }
    }
}
