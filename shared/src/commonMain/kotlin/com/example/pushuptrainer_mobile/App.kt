package com.example.pushuptrainer_mobile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme

internal val LocalToggleTheme = staticCompositionLocalOf<() -> Unit> {
    error("Theme controller is unavailable")
}

@Composable
fun App(onDarkThemeChanged: (Boolean) -> Unit = {}) {
    val systemDark = isSystemInDarkTheme()
    var selectedDark by rememberSaveable { mutableStateOf<Boolean?>(null) }
    val dark = selectedDark ?: systemDark
    SideEffect { onDarkThemeChanged(dark) }
    PushUpTrainerTheme(darkTheme = dark) {
        CompositionLocalProvider(LocalToggleTheme provides { selectedDark = !dark }) {
            PushUpTrainerNavGraph()
        }
    }
}
