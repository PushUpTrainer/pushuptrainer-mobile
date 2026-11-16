package com.example.pushuptrainer_mobile.features.progress.domain.usecase

import com.example.pushuptrainer_mobile.features.progress.domain.model.ProgressHistory
import com.example.pushuptrainer_mobile.features.progress.domain.model.ProgressWorkout
import com.example.pushuptrainer_mobile.features.progress.domain.repository.ProgressRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

internal class ObserveProgressUseCase(
    private val repository: ProgressRepository,
    private val clock: Clock = Clock.System,
    private val timeZone: () -> TimeZone = { TimeZone.currentSystemDefault() },
) {
    operator fun invoke(selectedDate: LocalDate?) = combine(repository.observeWorkouts(), localDay()) { workouts, (today, zone) ->
        calculateProgress(workouts, today, zone, selectedDate ?: today)
    }

    private fun localDay() = flow {
        while (true) {
            val zone = timeZone()
            val today = clock.now().toLocalDateTime(zone).date
            emit(today to zone)
            val tomorrow = LocalDate.fromEpochDays(today.toEpochDays() + 1)
            delay((tomorrow.atStartOfDayIn(zone) - clock.now()).coerceIn(1.milliseconds, 1.minutes))
        }
    }.distinctUntilChanged()
}

internal fun calculateProgress(
    workouts: List<ProgressWorkout>,
    today: LocalDate,
    zone: TimeZone,
    selectedDate: LocalDate = today,
): ProgressHistory {
    val selected = minOf(selectedDate, today)
    val valid = workouts.filter { it.repetitions > 0 }
    val byDay = valid.groupBy { Instant.fromEpochMilliseconds(it.startedAtEpochMs).toLocalDateTime(zone).date }
    val day = byDay[selected].orEmpty()
    return ProgressHistory(
        today, selected, byDay.keys, day.size, day.sumOf { it.repetitions },
        valid.sumOf { it.repetitions.toLong() }, valid.maxOfOrNull { it.repetitions } ?: 0,
    )
}
