package com.example.pushuptrainer_mobile.features.main.presentation

import com.example.pushuptrainer_mobile.features.progress.ProgressRoute
import androidx.lifecycle.ViewModel
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar.BottomNavigationDestination
import com.example.pushuptrainer_mobile.features.about.AboutRoute
import com.example.pushuptrainer_mobile.features.home.HomeRoute
import com.example.pushuptrainer_mobile.features.home.navigateToHomeScreen
import com.example.pushuptrainer_mobile.features.preparation.PreparationRoute
import com.example.pushuptrainer_mobile.features.trainingresult.TrainingResultRoute
import com.example.pushuptrainer_mobile.features.workout.WorkoutRoute

internal class MainViewModel(private val router: Router) : ViewModel() {
    fun state(entry: NavigationEntry) = MainUiState(
        selected = when (entry.destination) {
            HomeRoute, PreparationRoute, is WorkoutRoute, is TrainingResultRoute -> BottomNavigationDestination.Trainer
            ProgressRoute -> BottomNavigationDestination.Progress
            AboutRoute -> BottomNavigationDestination.About
            else -> error("Unknown Main destination: ${entry.destination}")
        },
        available = if (entry.destination is WorkoutRoute) emptySet() else setOf(BottomNavigationDestination.Trainer, BottomNavigationDestination.Progress, BottomNavigationDestination.About),
    )

    fun onEvent(entry: NavigationEntry, event: MainUiEvent): Boolean = if (entry.destination is WorkoutRoute) false else when (event) {
        is MainUiEvent.TabSelected -> when (event.destination) {
            BottomNavigationDestination.Trainer -> router.navigateToHomeScreen(entry)
            BottomNavigationDestination.Progress -> router.returnTo(entry, ProgressRoute) || router.open(entry, ProgressRoute)
            BottomNavigationDestination.About -> router.returnTo(entry, AboutRoute) || router.open(entry, AboutRoute)
        }
    }
}
