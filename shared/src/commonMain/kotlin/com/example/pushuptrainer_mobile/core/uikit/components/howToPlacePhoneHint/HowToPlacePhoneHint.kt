package com.example.pushuptrainer_mobile.core.uikit.components.howToPlacePhoneHint

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.components.primaryButton.AppPrimaryButton
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.preparation_hint_confirm
import pushuptrainermobile.shared.generated.resources.preparation_hint_step1_note
import pushuptrainermobile.shared.generated.resources.preparation_hint_step1_title
import pushuptrainermobile.shared.generated.resources.preparation_hint_step2_title
import pushuptrainermobile.shared.generated.resources.preparation_hint_step3_note
import pushuptrainermobile.shared.generated.resources.preparation_hint_step3_title
import pushuptrainermobile.shared.generated.resources.preparation_hint_title

/** Подсказка «Как поставить телефон»: инструкция по установке и кнопка «Понятно» (макет 1.1.0). */
@Composable
fun HowToPlacePhoneHint(
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AppTheme.colors.surface)
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(Res.string.preparation_hint_title),
            style = AppTheme.typography.cardTitleLarge,
            color = AppTheme.colors.textPrimary,
        )
        Spacer(Modifier.height(16.dp))
        HintStep(
            title = stringResource(Res.string.preparation_hint_step1_title),
            note = stringResource(Res.string.preparation_hint_step1_note),
        )
        Spacer(Modifier.height(14.dp))
        HintStep(
            title = stringResource(Res.string.preparation_hint_step2_title),
            note = null,
        )
        Spacer(Modifier.height(14.dp))
        HintStep(
            title = stringResource(Res.string.preparation_hint_step3_title),
            note = stringResource(Res.string.preparation_hint_step3_note),
        )
        Spacer(Modifier.height(24.dp))
        AppPrimaryButton(
            text = stringResource(Res.string.preparation_hint_confirm),
            onClick = onConfirmClick,
        )
    }
}

@Composable
private fun HintStep(
    title: String,
    note: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            text = title,
            style = AppTheme.typography.cardTitle,
            color = AppTheme.colors.textPrimary,
        )
        if (note != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = note,
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
    }
}

@Preview
@Composable
private fun HowToPlacePhoneHintLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            HowToPlacePhoneHint(onConfirmClick = {}, modifier = Modifier.padding(20.dp))
        }
    }
}

@Preview
@Composable
private fun HowToPlacePhoneHintDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            HowToPlacePhoneHint(onConfirmClick = {}, modifier = Modifier.padding(20.dp))
        }
    }
}
