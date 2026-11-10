package com.example.pushuptrainer_mobile.features.about.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination

/** Действия пользователя на экране «О проекте». */
sealed interface AboutUiEvent {

    /** Нажата кнопка «Тема». */
    data object ThemeToggleClicked : AboutUiEvent

    /** Нажата ссылка на репозиторий проекта. */
    data object RepositoryLinkClicked : AboutUiEvent

    /** Выбран раздел в нижней навигации. */
    data class BottomBarDestinationClicked(
        val destination: BottomNavigationDestination,
    ) : AboutUiEvent
}
