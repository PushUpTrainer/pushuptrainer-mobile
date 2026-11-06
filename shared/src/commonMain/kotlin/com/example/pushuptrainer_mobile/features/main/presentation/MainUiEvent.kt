package com.example.pushuptrainer_mobile.features.main.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination

/** Действия пользователя на главном экране. */
sealed interface MainUiEvent {

    /** Нажата кнопка «Подготовиться». */
    data object PrepareClicked : MainUiEvent

    /** Нажата карточка «Как работает приложение». */
    data object HowItWorksClicked : MainUiEvent

    /** Нажата кнопка «Тема». */
    data object ThemeToggleClicked : MainUiEvent

    /** Выбран раздел в нижней навигации. */
    data class BottomBarDestinationClicked(
        val destination: BottomNavigationDestination,
    ) : MainUiEvent
}
