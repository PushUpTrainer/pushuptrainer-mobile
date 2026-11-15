package com.example.pushuptrainer_mobile.features.home.data.repository

import com.example.pushuptrainer_mobile.data.local.datasource.WorkoutLocalDataSource
import com.example.pushuptrainer_mobile.features.home.domain.model.HomeHistory
import com.example.pushuptrainer_mobile.features.home.domain.model.HomeWorkout
import com.example.pushuptrainer_mobile.features.home.domain.repository.HomeRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime

internal class HomeRepositoryImpl(
    private val source: WorkoutLocalDataSource,
    private val clock: Clock = Clock.System,
    private val timeZone: () -> TimeZone = { TimeZone.currentSystemDefault() },
) : HomeRepository {
    override fun observeHistory(selectedDate: LocalDate?): Flow<HomeHistory> =
        combine(source.observeSessions(), observeLocalDay()) { sessions, (today, zone) ->
            calculateHomeHistory(
                workouts = sessions.map { HomeWorkout(it.session.startedAtEpochMs, it.session.repetitions) },
                today = today,
                zone = zone,
                selectedDate = selectedDate ?: today,
            )
        }

    private fun observeLocalDay(): Flow<Pair<LocalDate, TimeZone>> = flow {
        while (true) {
            val zone = timeZone()
            val today = clock.now().toLocalDateTime(zone).date
            emit(today to zone)
            val tomorrow = LocalDate.fromEpochDays(today.toEpochDays() + 1)
            delay((tomorrow.atStartOfDayIn(zone) - clock.now()).coerceIn(1.milliseconds, 1.minutes))
        }
    }.distinctUntilChanged()
}

internal fun calculateHomeHistory(
    workouts: List<HomeWorkout>,
    today: LocalDate,
    zone: TimeZone,
    selectedDate: LocalDate = today,
): HomeHistory {
    val selected = minOf(selectedDate, today)
    val monday = selected.toEpochDays() - (selected.dayOfWeek.isoDayNumber - 1)
    val byDay = workouts.groupBy {
        Instant.fromEpochMilliseconds(it.startedAtEpochMs).toLocalDateTime(zone).date
    }
    val selectedWorkouts = byDay[selected].orEmpty()
    return HomeHistory(
        week = (0..6).map { LocalDate.fromEpochDays(monday + it) },
        today = today,
        selectedDate = selected,
        workoutDays = byDay.keys,
        repetitions = selectedWorkouts.sumOf { it.repetitions },
        workouts = selectedWorkouts.size,
        streakDays = byDay.keys.streakEndingAt(today),
        hasWorkoutToday = today in byDay,
    )
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
