package com.example.pushuptrainer_mobile.features.home.domain.usecase

import com.example.pushuptrainer_mobile.features.home.domain.model.HomeWorkout
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.Test
import kotlin.test.assertEquals

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
        assertEquals(0, before.workoutsToday)
        assertEquals(0, before.repetitionsToday)

        val after = calculateHomeHistory(previous + workout(monday, 10) + workout(monday, 6), monday, zone)
        assertEquals(3, after.streakDays)
        assertEquals(2, after.workoutsToday)
        assertEquals(16, after.repetitionsToday)
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

    private fun workout(day: LocalDate, repetitions: Int) =
        HomeWorkout(day.atStartOfDayIn(zone).toEpochMilliseconds(), repetitions)
}
