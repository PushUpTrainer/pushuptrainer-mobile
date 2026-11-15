package com.example.pushuptrainer_mobile.features.about.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.teamCard.TeamCardUiModel

/** Состояние экрана «О проекте». */
sealed interface AboutUiState {

    /** Данные экрана ещё подготавливаются. */
    data object Loading : AboutUiState

    /** Экран готов к отображению. */
    data class Content(
        val teams: List<TeamCardUiModel>,
        val repositoryUrl: String,
    ) : AboutUiState
}
