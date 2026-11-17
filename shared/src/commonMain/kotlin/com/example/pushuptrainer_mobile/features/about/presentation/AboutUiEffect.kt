package com.example.pushuptrainer_mobile.features.about.presentation

sealed interface AboutUiEffect {
    data class OpenRepository(val url: String) : AboutUiEffect
}
