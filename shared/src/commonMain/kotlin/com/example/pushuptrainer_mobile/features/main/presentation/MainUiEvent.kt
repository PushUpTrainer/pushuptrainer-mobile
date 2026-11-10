package com.example.pushuptrainer_mobile.features.main.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination

internal sealed interface MainUiEvent {
    data class TabSelected(val destination: BottomNavigationDestination) : MainUiEvent
}
