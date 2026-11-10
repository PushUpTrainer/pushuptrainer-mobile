package com.example.pushuptrainer_mobile.core.uikit.components.teamCard

import org.jetbrains.compose.resources.DrawableResource

/** Данные карточки направления команды: иконка, название и группы участников. */
data class TeamCardUiModel(
    val icon: DrawableResource,
    val title: String,
    val groups: List<TeamGroupUiModel>,
)

/**
 * Группа участников внутри направления.
 *
 * @param role подпись над именами, например «Android-разработка».
 * `null`, если у направления нет разделения по ролям (как у «UI-дизайн»).
 */
data class TeamGroupUiModel(
    val role: String?,
    val members: List<String>,
)
