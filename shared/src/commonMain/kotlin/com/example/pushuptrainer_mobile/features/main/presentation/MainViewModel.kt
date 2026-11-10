package com.example.pushuptrainer_mobile.features.main.presentation

import androidx.lifecycle.ViewModel
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination
import com.example.pushuptrainer_mobile.features.home.HomeRoute
import com.example.pushuptrainer_mobile.features.home.navigateToHomeScreen
import com.example.pushuptrainer_mobile.features.trainingresult.TrainingResultRoute

internal class MainViewModel(private val router: Router) : ViewModel() {
    fun state(entry: NavigationEntry) = MainUiState(
        selected = when (entry.destination) {
            HomeRoute, is TrainingResultRoute -> BottomNavigationDestination.Trainer
            else -> error("Unknown Main destination: ${entry.destination}")
        },
        available = setOf(BottomNavigationDestination.Trainer),
    )

    fun onEvent(entry: NavigationEntry, event: MainUiEvent): Boolean = when (event) {
        is MainUiEvent.TabSelected -> when (event.destination) {
            BottomNavigationDestination.Trainer -> router.navigateToHomeScreen(entry)
            BottomNavigationDestination.Progress, BottomNavigationDestination.About -> false
        }
    }
}
