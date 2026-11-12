package com.example.pushuptrainer_mobile.features.onboarding

import androidx.navigation3.runtime.EntryProviderScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.core.navigation.LocalNavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.features.onboarding.presentation.OnboardingRoute as OnboardingContent
import com.example.pushuptrainer_mobile.features.onboarding.presentation.OnboardingViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
internal data object OnboardingRoute : Destination

internal fun Router.navigateToOnboardingScreen(from: NavigationEntry): Boolean = open(from, OnboardingRoute)

internal fun EntryProviderScope<Destination>.onboardingSection() {
    entry<OnboardingRoute> {
        val entry = LocalNavigationEntry.current
        OnboardingContent(viewModel = koinViewModel<OnboardingViewModel> { parametersOf(entry) })
    }
}
