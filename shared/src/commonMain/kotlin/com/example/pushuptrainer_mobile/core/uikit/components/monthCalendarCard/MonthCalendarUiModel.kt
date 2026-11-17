package com.example.pushuptrainer_mobile.core.uikit.components.monthCalendarCard

/**
 * Данные календаря месяца.
 *
 * @param title подпись месяца, например «Октябрь 2026».
 * @param weeks недели с понедельника по воскресенье; в каждой ровно 7 ячеек,
 * пустые ячейки до 1-го и после последнего числа равны null.
 */
data class MonthCalendarUiModel(
    val title: String,
    val weeks: List<List<MonthDayUiModel?>>,
    val canGoForward: Boolean = true,
)

/** Один день в календаре месяца. */
data class MonthDayUiModel(
    val dayOfMonth: Int,
    val period: MonthDayPeriodUiModel,
    val hasWorkout: Boolean,
    val epochDay: Long? = null,
    val isSelected: Boolean = period == MonthDayPeriodUiModel.Today,
    val dateLabel: String = dayOfMonth.toString(),
)

/** Положение дня относительно сегодняшнего. */
enum class MonthDayPeriodUiModel {
    Past,
    Today,
    Future,
}
