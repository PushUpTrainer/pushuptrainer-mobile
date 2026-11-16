package com.example.pushuptrainer_mobile.features.progress

import com.example.pushuptrainer_mobile.features.progress.domain.model.ProgressWorkout
import com.example.pushuptrainer_mobile.features.progress.domain.usecase.calculateProgress
import com.example.pushuptrainer_mobile.features.progress.presentation.toUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

class ProgressHistoryTest {
    @Test
    fun emptyAndLegacyZeroSessionsDoNotInflateStatistics() {
        val today = LocalDate(2026, 11, 16)
        val history = calculateProgress(listOf(workout(today, 0)), today, TimeZone.UTC)
        assertEquals(0, history.dayWorkouts)
        assertEquals(0, history.dayRepetitions)
        assertEquals(0L, history.totalRepetitions)
        assertEquals(0, history.bestWorkout)
        assertTrue(history.workoutDays.isEmpty())
    }

    @Test
    fun selectedDayAndAllTimeRecordUseOnlyPositiveSessions() {
        val today = LocalDate(2026, 11, 16)
        val before = LocalDate(2026, 10, 31)
        val history = calculateProgress(listOf(workout(today, 5), workout(today, 7), workout(before, 20), workout(before, 0)), today, TimeZone.UTC, before)
        assertEquals(1, history.dayWorkouts)
        assertEquals(20, history.dayRepetitions)
        assertEquals(32L, history.totalRepetitions)
        assertEquals(20, history.bestWorkout)
        assertEquals(setOf(today, before), history.workoutDays)
    }

    @Test
    fun calendarHandlesLeapYearSixRowsAndYearBoundary() {
        val today = LocalDate(2028, 3, 1)
        val feb = calculateProgress(emptyList(), today, TimeZone.UTC, LocalDate(2028, 2, 29)).toUiState().calendar
        assertEquals(29, feb.weeks.flatten().filterNotNull().size)
        assertTrue(feb.weeks.all { it.size == 7 })
        assertTrue(feb.canGoForward)
        val december = calculateProgress(emptyList(), LocalDate(2026, 12, 31), TimeZone.UTC).toUiState().calendar
        assertEquals("Декабрь 2026", december.title)
        assertFalse(december.canGoForward)
        val august = calculateProgress(emptyList(), LocalDate(2026, 8, 31), TimeZone.UTC).toUiState().calendar
        assertEquals(6, august.weeks.size)
    }

    @Test
    fun localMidnightAndFutureDateAreHandled() {
        val today = LocalDate(2026, 11, 16)
        val late = ProgressWorkout(Instant.parse("2026-11-15T22:30:00Z").toEpochMilliseconds(), 9)
        val history = calculateProgress(listOf(late), today, TimeZone.of("Europe/Moscow"), LocalDate(2026, 12, 1))
        assertEquals(today, history.selectedDate)
        assertEquals(9, history.dayRepetitions)
        assertEquals(0, calculateProgress(listOf(late), today, TimeZone.UTC).dayRepetitions)
    }

    private fun workout(day: LocalDate, repetitions: Int) = ProgressWorkout(day.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(), repetitions)
}
