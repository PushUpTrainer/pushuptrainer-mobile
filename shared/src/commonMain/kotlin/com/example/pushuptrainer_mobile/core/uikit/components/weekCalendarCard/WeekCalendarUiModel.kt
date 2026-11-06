package com.example.pushuptrainer_mobile.core.uikit.components.weekCalendarCard

/** Данные карточки «Твоя неделя». */
data class WeekCalendarUiModel(
    /** Подпись диапазона дат, например «19–25 октября». */
    val rangeText: String,
    /** Дни недели с понедельника по воскресенье. */
    val days: List<WeekDayUiModel>,
)

/** Один день в календаре недели. */
data class WeekDayUiModel(
    val dayOfMonth: Int,
    val isToday: Boolean,
    val hasWorkout: Boolean,
)
