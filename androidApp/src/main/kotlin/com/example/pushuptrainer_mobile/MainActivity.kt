package com.example.pushuptrainer_mobile

import android.os.Bundle
import androidx.compose.runtime.CompositionLocalProvider
import com.example.pushuptrainer_mobile.ml.presentation.LocalWorkoutCamera
import com.example.pushuptrainer_mobile.ml.presentation.AndroidWorkoutCamera
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(LocalWorkoutCamera provides { AndroidWorkoutCamera(it) }) {
                App { dark ->
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !dark
                        isAppearanceLightNavigationBars = !dark
                    }
                }
            }
        }
    }
}
