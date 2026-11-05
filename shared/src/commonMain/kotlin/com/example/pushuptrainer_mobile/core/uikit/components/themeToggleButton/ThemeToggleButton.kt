package com.example.pushuptrainer_mobile.core.uikit.components.themeToggleButton

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.ic_theme_sun
import pushuptrainermobile.shared.generated.resources.theme_toggle

/** Кнопка «Тема» в шапке экрана: переключает светлую/тёмную тему. */
@Composable
fun ThemeToggleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(14.dp),
        color = AppTheme.colors.accentContainer,
        border = BorderStroke(1.dp, AppTheme.colors.accent),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_theme_sun),
                contentDescription = null,
                tint = AppTheme.colors.accentText,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = stringResource(Res.string.theme_toggle),
                style = AppTheme.typography.labelMedium,
                color = AppTheme.colors.accentText,
            )
        }
    }
}

@Preview
@Composable
private fun ThemeToggleButtonLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            ThemeToggleButton(onClick = {}, modifier = Modifier.padding(20.dp))
        }
    }
}

@Preview
@Composable
private fun ThemeToggleButtonDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            ThemeToggleButton(onClick = {}, modifier = Modifier.padding(20.dp))
        }
    }
}
