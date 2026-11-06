package com.example.pushuptrainer_mobile.core.uikit.components.hintCard

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.painterResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.ic_chevron_right
import pushuptrainermobile.shared.generated.resources.ic_guide_book

/**
 * Нажимаемая карточка с книжкой и стрелкой:
 * «Как работает приложение», «Как поставить телефон».
 */
@Composable
fun HintCard(
    model: HintCardUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(16.dp),
        color = AppTheme.colors.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_guide_book),
                contentDescription = null,
                tint = AppTheme.colors.textPrimary,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = model.title,
                    style = AppTheme.typography.labelMedium,
                    color = AppTheme.colors.textPrimary,
                )
                Text(
                    text = model.subtitle,
                    style = AppTheme.typography.caption,
                    color = AppTheme.colors.textSecondary,
                )
            }
            Icon(
                painter = painterResource(Res.drawable.ic_chevron_right),
                contentDescription = null,
                tint = AppTheme.colors.textSecondary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview
@Composable
private fun HintCardLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        HintCardPreviewContent()
    }
}

@Preview
@Composable
private fun HintCardDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        HintCardPreviewContent()
    }
}

@Composable
private fun HintCardPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        HintCard(
            model = HintCardUiModel(
                title = "Как работает приложение",
                subtitle = "Четыре коротких шага",
            ),
            onClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
