package com.example.pushuptrainer_mobile.core.uikit.components.statTile

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.ic_repeat
import pushuptrainermobile.shared.generated.resources.ic_trophy

/** Плитка статистики 56 dp высотой: значок и число сверху, подпись снизу. */
@Composable
fun StatTile(
    model: StatTileUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.colors.surface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(model.icon.iconResource()),
                contentDescription = null,
                tint = AppTheme.colors.accent,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = model.value,
                style = AppTheme.typography.statValue,
                color = AppTheme.colors.textPrimary,
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = model.caption,
            style = AppTheme.typography.captionSmall,
            color = AppTheme.colors.textSecondary,
            maxLines = 1,
        )
    }
}

private fun StatTileIconUiModel.iconResource(): DrawableResource = when (this) {
    StatTileIconUiModel.Repetitions -> Res.drawable.ic_repeat
    StatTileIconUiModel.Record -> Res.drawable.ic_trophy
}

@Preview
@Composable
private fun StatTileDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        StatTilePreviewContent()
    }
}

@Preview
@Composable
private fun StatTileLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        StatTilePreviewContent()
    }
}

@Composable
private fun StatTilePreviewContent() {
    Surface(color = AppTheme.colors.background) {
        Row(
            modifier = Modifier.padding(16.dp).width(320.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatTile(
                model = StatTileUiModel(StatTileIconUiModel.Repetitions, "86", "Всего повторений"),
                modifier = Modifier.weight(1f),
            )
            StatTile(
                model = StatTileUiModel(StatTileIconUiModel.Record, "16", "Рекорд"),
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
        }
    }
}
