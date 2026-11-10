package com.example.pushuptrainer_mobile.features.home.presentation

sealed interface HomeUiEvent {
    data object PrepareClicked : HomeUiEvent
}
