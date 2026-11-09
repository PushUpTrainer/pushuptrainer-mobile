package com.example.pushuptrainer_mobile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.example.pushuptrainer_mobile.core.navigation.NavigationRoot
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.compose_multiplatform

@Serializable
private data object StartupRoute : NavKey

private val navigationConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(StartupRoute::class, StartupRoute.serializer())
        }
    }
}

@Composable
fun App(greeting: Greeting = koinInject()) {
    NavigationRoot(
        startDestination = StartupRoute,
        savedStateConfiguration = navigationConfiguration,
    ) {
        entry<StartupRoute> { StartupScreen(greeting) }
    }
}

@Composable
private fun StartupScreen(greeting: Greeting) {
    MaterialTheme {
        var showContent by rememberSaveable { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greetingText = remember(greeting) { greeting.greet() }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greetingText")
                }
            }
        }
    }
}

@Composable
@Preview
private fun AppPreview() {
    StartupScreen(greeting = Greeting())
}
