package com.example.pushuptrainer_mobile.core.uikit.components.howToPlacePhoneRow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.preparation_how_to_place_subtitle
import pushuptrainermobile.shared.generated.resources.preparation_how_to_place_title

/** Строка-подсказка «Как поставить телефон» с переходом на инструкцию. */
@Composable
fun HowToPlacePhoneRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.preparation_how_to_place_title),
                style = AppTheme.typography.cardTitle,
                color = AppTheme.colors.textPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(Res.string.preparation_how_to_place_subtitle),
                style = AppTheme.typography.caption,
                color = AppTheme.colors.textSecondary,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = "›",
            style = AppTheme.typography.cardTitleLarge,
            color = AppTheme.colors.textSecondary,
        )
    }
}

@Preview
@Composable
private fun HowToPlacePhoneRowLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            HowToPlacePhoneRow(onClick = {}, modifier = Modifier.padding(20.dp))
        }
    }
}

@Preview
@Composable
private fun HowToPlacePhoneRowDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            HowToPlacePhoneRow(onClick = {}, modifier = Modifier.padding(20.dp))
        }
    }
}
