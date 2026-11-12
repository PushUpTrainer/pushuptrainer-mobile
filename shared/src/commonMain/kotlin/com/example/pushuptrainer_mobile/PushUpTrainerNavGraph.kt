package com.example.pushuptrainer_mobile

import androidx.compose.runtime.Composable
import com.example.pushuptrainer_mobile.core.navigation.NavigationRoot
import com.example.pushuptrainer_mobile.core.navigation.RouterController
import com.example.pushuptrainer_mobile.core.navigation.di.GlobalRouterContext
import com.example.pushuptrainer_mobile.core.navigation.navigationStateConfiguration
import com.example.pushuptrainer_mobile.features.main.MainRoute
import com.example.pushuptrainer_mobile.features.main.mainSection
import com.example.pushuptrainer_mobile.features.onboarding.OnboardingRoute
import com.example.pushuptrainer_mobile.features.onboarding.onboardingSection
import org.koin.compose.koinInject
import org.koin.core.qualifier.qualifier

private val globalState = navigationStateConfiguration {
    subclass(MainRoute::class, MainRoute.serializer())
    subclass(OnboardingRoute::class, OnboardingRoute.serializer())
}

@Composable
internal fun PushUpTrainerNavGraph() {
    NavigationRoot(
        controller = koinInject<RouterController>(qualifier<GlobalRouterContext>()),
        startDestination = MainRoute,
        savedStateConfiguration = globalState,
        registerEntries = {
            mainSection()
            onboardingSection()
        },
    )
}
