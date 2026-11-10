package com.example.pushuptrainer_mobile.features.about.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.core.uikit.components.teamCard.TeamCardUiModel
import com.example.pushuptrainer_mobile.core.uikit.components.teamCard.TeamGroupUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.about_role_algorithms
import pushuptrainermobile.shared.generated.resources.about_role_android
import pushuptrainermobile.shared.generated.resources.about_role_ios
import pushuptrainermobile.shared.generated.resources.about_role_ml
import pushuptrainermobile.shared.generated.resources.about_team_design
import pushuptrainermobile.shared.generated.resources.about_team_ml
import pushuptrainermobile.shared.generated.resources.about_team_mobile
import pushuptrainermobile.shared.generated.resources.ic_team_design
import pushuptrainermobile.shared.generated.resources.ic_team_mobile
import pushuptrainermobile.shared.generated.resources.ic_team_ml

/**
 * ViewModel экрана «О проекте».
 *
 * Состав команды статичный, поэтому ViewModel сразу собирает его из ресурсов.
 * Открытие ссылки, навигация и переключение темы подключаются отдельными задачами.
 */
class AboutViewModel : ViewModel() {

    private val _state = MutableStateFlow<AboutUiState>(AboutUiState.Loading)
    val state: StateFlow<AboutUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = AboutUiState.Content(
                teams = loadTeams(),
                repositoryUrl = REPOSITORY_URL,
            )
        }
    }

    fun onEvent(event: AboutUiEvent) {
        when (event) {
            AboutUiEvent.RepositoryLinkClicked -> Unit
        }
    }

    private suspend fun loadTeams(): List<TeamCardUiModel> = listOf(
        TeamCardUiModel(
            icon = Res.drawable.ic_team_mobile,
            title = getString(Res.string.about_team_mobile),
            groups = listOf(
                TeamGroupUiModel(
                    role = getString(Res.string.about_role_android),
                    members = listOf("Соколов Никита", "Деенкова Виолетта"),
                ),
                TeamGroupUiModel(
                    role = getString(Res.string.about_role_ios),
                    members = listOf("Плисс Полина", "Пичковская Дарья"),
                ),
            ),
        ),
        TeamCardUiModel(
            icon = Res.drawable.ic_team_ml,
            title = getString(Res.string.about_team_ml),
            groups = listOf(
                TeamGroupUiModel(
                    role = getString(Res.string.about_role_ml),
                    members = listOf("Тараканов Алексей", "Коновалов Александр"),
                ),
                TeamGroupUiModel(
                    role = getString(Res.string.about_role_algorithms),
                    members = listOf("Никифорова Екатерина"),
                ),
            ),
        ),
        TeamCardUiModel(
            icon = Res.drawable.ic_team_design,
            title = getString(Res.string.about_team_design),
            groups = listOf(
                TeamGroupUiModel(role = null, members = listOf("Борнусов Иван")),
            ),
        ),
    )

    private companion object {
        const val REPOSITORY_URL = "https://github.com/PushUpTrainer"
    }
}
