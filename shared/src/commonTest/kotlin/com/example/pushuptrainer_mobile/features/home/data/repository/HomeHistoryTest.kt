package com.example.pushuptrainer_mobile.features.home.data.repository

import com.example.pushuptrainer_mobile.features.home.domain.model.HomeWorkout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

class HomeHistoryTest {
    private val zone = TimeZone.of("Europe/Moscow")
    private val monday = LocalDate(2026, 11, 9)
    private val sunday = LocalDate(2026, 11, 8)
    private val saturday = LocalDate(2026, 11, 7)

    @Test
    fun streakContinuesFromYesterdayAndCountsEachDayOnce() {
        val previous = listOf(workout(saturday, 5), workout(sunday, 8), workout(sunday, 7))
        val before = calculateHomeHistory(previous, monday, zone)
        assertEquals(2, before.streakDays)
        assertEquals(0, before.workouts)
        assertEquals(0, before.repetitions)

        val after = calculateHomeHistory(previous + workout(monday, 10) + workout(monday, 6), monday, zone)
        assertEquals(3, after.streakDays)
        assertEquals(2, after.workouts)
        assertEquals(16, after.repetitions)
        assertEquals(setOf(saturday, sunday, monday), after.workoutDays)
        assertEquals(monday, after.week.first())
        assertEquals(LocalDate(2026, 11, 15), after.week.last())
    }

    @Test
    fun streakResetsAfterAMissedDay() {
        val previous = listOf(workout(saturday, 8))
        assertEquals(0, calculateHomeHistory(emptyList(), monday, zone).streakDays)
        assertEquals(0, calculateHomeHistory(previous, monday, zone).streakDays)
        assertEquals(1, calculateHomeHistory(previous + workout(monday, 5), monday, zone).streakDays)
    }

    @Test
    fun selectedDayReadsOnlyItsWorkoutsAcrossWeekAndYearBoundaries() {
        val today = LocalDate(2027, 1, 4)
        val previousYear = LocalDate(2026, 12, 31)
        val history = calculateHomeHistory(
            listOf(workout(previousYear, 7), workout(previousYear, 4), workout(today, 20)),
            today,
            zone,
            previousYear,
        )
        assertEquals(previousYear, history.selectedDate)
        assertEquals(11, history.repetitions)
        assertEquals(2, history.workouts)
        assertEquals(LocalDate(2026, 12, 28), history.week.first())
        assertEquals(LocalDate(2027, 1, 3), history.week.last())
        assertEquals(1, history.streakDays)
        val empty = calculateHomeHistory(emptyList(), today, zone, previousYear)
        assertEquals(0, empty.repetitions)
        assertEquals(0, empty.workouts)
    }

    @Test
    fun localTimezoneDeterminesWorkoutDayAndFutureSelectionIsClamped() {
        val timestamp = kotlin.time.Instant.parse("2026-11-08T22:30:00Z").toEpochMilliseconds()
        val workouts = listOf(HomeWorkout(timestamp, 12))
        assertEquals(12, calculateHomeHistory(workouts, monday, zone).repetitions)
        assertEquals(0, calculateHomeHistory(workouts, monday, TimeZone.UTC).repetitions)
        val future = calculateHomeHistory(workouts, monday, zone, LocalDate(2026, 11, 20))
        assertEquals(monday, future.selectedDate)
        assertEquals(12, future.repetitions)
    }

    private fun workout(day: LocalDate, repetitions: Int) =
        HomeWorkout(day.atStartOfDayIn(zone).toEpochMilliseconds(), repetitions)
}
