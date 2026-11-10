package com.example.pushuptrainer_mobile.features.home.domain

import com.example.pushuptrainer_mobile.domain.workout.WorkoutHistoryRepository
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime

internal data class HomeHistory(
    val week: List<LocalDate>,
    val today: LocalDate,
    val workoutDays: Set<LocalDate>,
    val repetitionsToday: Int,
    val workoutsToday: Int,
)

internal class ObserveHomeHistory(private val repository: WorkoutHistoryRepository) {
    operator fun invoke() = repository.observe().map { workouts ->
        val zone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(zone).date
        val monday = today.toEpochDays() - (today.dayOfWeek.isoDayNumber - 1)
        val byDay = workouts.groupBy {
            Instant.fromEpochMilliseconds(it.startedAtEpochMs).toLocalDateTime(zone).date
        }
        val todayWorkouts = byDay[today].orEmpty()
        HomeHistory(
            week = (0..6).map { LocalDate.fromEpochDays(monday + it) },
            today = today,
            workoutDays = byDay.keys,
            repetitionsToday = todayWorkouts.sumOf { it.repetitions },
            workoutsToday = todayWorkouts.size,
        )
    }
}
