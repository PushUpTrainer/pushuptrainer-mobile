package com.example.pushuptrainer_mobile.features.home.domain.usecase

import com.example.pushuptrainer_mobile.features.home.domain.model.HomeHistory
import com.example.pushuptrainer_mobile.features.home.domain.model.HomeWorkout
import com.example.pushuptrainer_mobile.features.home.domain.repository.HomeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

internal class ObserveHomeHistoryUseCase(private val repository: HomeRepository) {
    operator fun invoke(): Flow<HomeHistory> =
        combine(repository.observeWorkouts(), observeLocalDay()) { workouts, (today, zone) ->
            calculateHomeHistory(workouts, today, zone)
        }
}

internal fun calculateHomeHistory(
    workouts: List<HomeWorkout>,
    today: LocalDate,
    zone: TimeZone,
): HomeHistory {
    val monday = today.toEpochDays() - (today.dayOfWeek.isoDayNumber - 1)
    val byDay = workouts.groupBy {
        Instant.fromEpochMilliseconds(it.startedAtEpochMs).toLocalDateTime(zone).date
    }
    val todayWorkouts = byDay[today].orEmpty()
    return HomeHistory(
        week = (0..6).map { LocalDate.fromEpochDays(monday + it) },
        today = today,
        workoutDays = byDay.keys,
        repetitionsToday = todayWorkouts.sumOf { it.repetitions },
        workoutsToday = todayWorkouts.size,
        streakDays = byDay.keys.streakEndingAt(today),
    )
}

private fun observeLocalDay(): Flow<Pair<LocalDate, TimeZone>> = flow {
    while (true) {
        val now = Clock.System.now()
        val zone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(zone).date
        emit(today to zone)
        val tomorrow = LocalDate.fromEpochDays(today.toEpochDays() + 1)
        delay((tomorrow.atStartOfDayIn(zone) - Clock.System.now()).coerceAtLeast(1.milliseconds))
    }
}

private fun Set<LocalDate>.streakEndingAt(today: LocalDate): Int {
    var day = today.toEpochDays()
    if (today !in this) day--
    var streak = 0
    while (LocalDate.fromEpochDays(day) in this) {
        streak++
        day--
    }
    return streak
}
