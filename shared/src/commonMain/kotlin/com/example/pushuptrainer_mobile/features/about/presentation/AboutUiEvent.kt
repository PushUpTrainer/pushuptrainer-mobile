package com.example.pushuptrainer_mobile.features.about.presentation

/** Действия пользователя на экране «О проекте». */
sealed interface AboutUiEvent {

    /** Нажата ссылка на репозиторий проекта. */
    data object RepositoryLinkClicked : AboutUiEvent
}
