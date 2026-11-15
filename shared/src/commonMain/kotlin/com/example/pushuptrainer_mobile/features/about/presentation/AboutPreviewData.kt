package com.example.pushuptrainer_mobile.features.about.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.teamCard.TeamCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.teamCard.TeamGroupUiModel
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.ic_team_design
import pushuptrainermobile.shared.generated.resources.ic_team_mobile
import pushuptrainermobile.shared.generated.resources.ic_team_ml

/** Тестовые данные для Preview экрана «О проекте» (значения из макета Figma 3.0). */
internal object AboutPreviewData {

    val content = AboutUiState.Content(
        teams = listOf(
            TeamCardUiModel(
                icon = Res.drawable.ic_team_mobile,
                title = "Мобильная разработка",
                groups = listOf(
                    TeamGroupUiModel(
                        role = "Android-разработка",
                        members = listOf("Соколов Никита", "Деенкова Виолетта"),
                    ),
                    TeamGroupUiModel(
                        role = "iOS-разработка",
                        members = listOf("Плисс Полина", "Пичковская Дарья"),
                    ),
                ),
            ),
            TeamCardUiModel(
                icon = Res.drawable.ic_team_ml,
                title = "Машинное обучение",
                groups = listOf(
                    TeamGroupUiModel(
                        role = "ML-разработка",
                        members = listOf("Тараканов Алексей", "Коновалов Александр"),
                    ),
                    TeamGroupUiModel(
                        role = "Разработка алгоритмов",
                        members = listOf("Никифорова Екатерина"),
                    ),
                ),
            ),
            TeamCardUiModel(
                icon = Res.drawable.ic_team_design,
                title = "UI-дизайн",
                groups = listOf(TeamGroupUiModel(role = null, members = listOf("Борнусов Иван"))),
            ),
        ),
        repositoryUrl = "https://github.com/PushUpTrainer",
    )
}
