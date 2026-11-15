package com.example.pushuptrainer_mobile.core.uikit.components.teamCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.painterResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.ic_team_design
import pushuptrainermobile.shared.generated.resources.ic_team_mobile

/** Карточка направления команды: «Мобильная разработка», «Машинное обучение», «UI-дизайн». */
@Composable
fun TeamCard(
    model: TeamCardUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.colors.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(model.icon),
                contentDescription = null,
                tint = AppTheme.colors.textPrimary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = model.title,
                style = AppTheme.typography.cardTitle,
                color = AppTheme.colors.textPrimary,
            )
        }
        model.groups.forEach { group ->
            TeamGroup(group)
        }
    }
}

@Composable
private fun TeamGroup(group: TeamGroupUiModel) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        group.role?.let { role ->
            Text(
                text = role,
                style = AppTheme.typography.captionMedium,
                color = AppTheme.colors.textSecondary,
            )
        }
        Text(
            text = group.members.joinToString(separator = "\n"),
            style = AppTheme.typography.body,
            color = AppTheme.colors.textPrimary,
        )
    }
}

@Preview
@Composable
private fun TeamCardLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        TeamCardPreviewContent()
    }
}

@Preview
@Composable
private fun TeamCardDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        TeamCardPreviewContent()
    }
}

@Composable
private fun TeamCardPreviewContent() {
    Surface(color = AppTheme.colors.background) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TeamCard(
                model = TeamCardUiModel(
                    icon = Res.drawable.ic_team_mobile,
                    title = "Мобильная разработка",
                    groups = listOf(
                        TeamGroupUiModel(role = "Android-разработка", members = listOf("Иванов Иван", "Петрова Анна")),
                        TeamGroupUiModel(role = "iOS-разработка", members = listOf("Сидоров Пётр")),
                    ),
                ),
            )
            TeamCard(
                model = TeamCardUiModel(
                    icon = Res.drawable.ic_team_design,
                    title = "UI-дизайн",
                    groups = listOf(TeamGroupUiModel(role = null, members = listOf("Смирнова Ольга"))),
                ),
            )
        }
    }
}
