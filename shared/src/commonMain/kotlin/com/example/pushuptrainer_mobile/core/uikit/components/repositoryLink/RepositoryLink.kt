package com.example.pushuptrainer_mobile.core.uikit.components.repositoryLink

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.painterResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.ic_github

/** Ссылка на репозиторий проекта: значок GitHub и подчёркнутый адрес. */
@Composable
fun RepositoryLink(
    url: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_github),
            contentDescription = null,
            tint = AppTheme.colors.textPrimary,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = url,
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colors.textPrimary,
            textDecoration = TextDecoration.Underline,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun RepositoryLinkLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            RepositoryLink(url = "https://github.com/PushUpTrainer", onClick = {})
        }
    }
}

@Preview
@Composable
private fun RepositoryLinkDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            RepositoryLink(url = "https://github.com/PushUpTrainer", onClick = {})
        }
    }
}
