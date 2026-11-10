package com.example.pushuptrainer_mobile.core.uikit.components.statTile

/** Данные плитки статистики: значок, число и подпись. */
data class StatTileUiModel(
    val icon: StatTileIconUiModel,
    val value: String,
    val caption: String,
)

/** Значок плитки статистики. */
enum class StatTileIconUiModel {
    /** Стрелки повторений. */
    Repetitions,

    /** Кубок рекорда. */
    Record,
}
