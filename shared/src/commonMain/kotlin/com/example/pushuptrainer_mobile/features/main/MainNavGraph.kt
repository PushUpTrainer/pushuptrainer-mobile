package com.example.pushuptrainer_mobile.features.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.pushuptrainer_mobile.core.navigation.LocalNavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.NavigationRoot
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.core.navigation.RouterController
import com.example.pushuptrainer_mobile.core.navigation.di.GlobalRouterContext
import com.example.pushuptrainer_mobile.core.navigation.navigationStateConfiguration
import com.example.pushuptrainer_mobile.features.home.HomeRoute
import com.example.pushuptrainer_mobile.features.home.homeSection
import com.example.pushuptrainer_mobile.features.main.di.MainRouterContext
import com.example.pushuptrainer_mobile.features.preparation.PreparationRoute
import com.example.pushuptrainer_mobile.features.preparation.preparationSection
import com.example.pushuptrainer_mobile.features.trainingresult.TrainingResultRoute
import com.example.pushuptrainer_mobile.features.trainingresult.trainingResultSection
import org.koin.compose.koinInject
import org.koin.core.qualifier.qualifier

internal val mainNavigationState = navigationStateConfiguration {
    subclass(HomeRoute::class, HomeRoute.serializer())
    subclass(PreparationRoute::class, PreparationRoute.serializer())
    subclass(TrainingResultRoute::class, TrainingResultRoute.serializer())
}

@Composable
internal fun MainNavGraph(content: @Composable (NavigationEntry, @Composable (Modifier) -> Unit) -> Unit) {
    val mainEntry = LocalNavigationEntry.current
    val globalRouter = koinInject<Router>(qualifier<GlobalRouterContext>())
    NavigationRoot(
        controller = koinInject<RouterController>(qualifier<MainRouterContext>()),
        startDestination = HomeRoute,
        savedStateConfiguration = mainNavigationState,
        registerEntries = {
            homeSection()
            preparationSection()
            trainingResultSection(preparation = PreparationRoute)
        },
        isActive = { globalRouter.isCurrent(mainEntry) },
        content = content,
    )
}
