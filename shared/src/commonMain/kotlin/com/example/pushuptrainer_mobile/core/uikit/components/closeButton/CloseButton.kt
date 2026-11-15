package com.example.pushuptrainer_mobile.core.uikit.components.closeButton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.close
import pushuptrainermobile.shared.generated.resources.ic_close

/** Кнопка-крестик 44×44: закрывает экран. */
@Composable
fun CloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(44.dp),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = stringResource(Res.string.close),
            tint = AppTheme.colors.textPrimary,
            modifier = Modifier.size(44.dp),
        )
    }
}

@Preview
@Composable
private fun CloseButtonDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            Box(Modifier.padding(16.dp)) { CloseButton(onClick = {}) }
        }
    }
}

@Preview
@Composable
private fun CloseButtonLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            Box(Modifier.padding(16.dp)) { CloseButton(onClick = {}) }
        }
    }
}
