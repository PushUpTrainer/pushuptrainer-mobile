package com.example.pushuptrainer_mobile.features.main.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination

internal data class MainUiState(
    val selected: BottomNavigationDestination,
    val available: Set<BottomNavigationDestination>,
)
