package com.example.pushuptrainer_mobile.features.progress

import com.example.pushuptrainer_mobile.features.progress.domain.model.ProgressWorkout
import com.example.pushuptrainer_mobile.features.progress.domain.usecase.calculateProgress
import com.example.pushuptrainer_mobile.features.progress.presentation.toUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import com.example.pushuptrainer_mobile.core.uikit.components.daySummaryCard.EmptyDayUiModel
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
        assertEquals(0, history.totalWorkouts)
        assertEquals(0.0, history.averageRepetitions)
        assertEquals(0, history.streakDays)
        assertTrue(history.toUiState().isHistoryEmpty)
        assertTrue(history.toUiState().streak.showStartPrompt)
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

    @Test
    fun streakCountsDaysAcrossYearBoundaryAndExpiresAfterMissedDay() {
        val today = LocalDate(2027, 1, 2)
        val workouts = listOf(
            workout(LocalDate(2026, 12, 30), 9),
            workout(LocalDate(2026, 12, 31), 10),
            workout(LocalDate(2026, 12, 31), 12),
            workout(LocalDate(2027, 1, 1), 11),
            workout(today, 0),
        )
        val history = calculateProgress(workouts, today, TimeZone.UTC, LocalDate(2026, 12, 30))
        assertEquals(3, history.streakDays)
        assertEquals(4, history.totalWorkouts)
        assertEquals(10.5, history.averageRepetitions)
        assertEquals("10,5", history.toUiState().averageRepetitions)
        assertFalse(history.toUiState().isStreakHighlighted)
        assertEquals(4, calculateProgress(workouts + workout(today, 8), today, TimeZone.UTC).streakDays)
        assertEquals(0, calculateProgress(workouts, LocalDate(2027, 1, 3), TimeZone.UTC).streakDays)
    }

    @Test
    fun streakStopsAtGapAndUsesLocalDay() {
        val today = LocalDate(2026, 11, 16)
        val workouts = listOf(
            workout(LocalDate(2026, 11, 13), 10),
            workout(LocalDate(2026, 11, 14), 0),
            workout(LocalDate(2026, 11, 15), 10),
            ProgressWorkout(Instant.parse("2026-11-15T22:30:00Z").toEpochMilliseconds(), 12),
        )
        assertEquals(2, calculateProgress(workouts, today, TimeZone.of("Europe/Moscow")).streakDays)
        assertEquals(1, calculateProgress(workouts, today, TimeZone.UTC).streakDays)
    }

    @Test
    fun emptyDayRetainsAllTimeMetricsAndAverageRoundsToOneDecimal() {
        val today = LocalDate(2026, 11, 16)
        val before = LocalDate(2026, 11, 15)
        val workouts = listOf(16, 16, 14, 14, 13, 13).map { workout(before, it) }
        val state = calculateProgress(workouts, today, TimeZone.UTC).toUiState()
        assertEquals("86", state.totalRepetitions)
        assertEquals("6", state.totalWorkouts)
        assertEquals("14,3", state.averageRepetitions)
        assertEquals("16", state.bestWorkout)
        assertEquals(EmptyDayUiModel.Today, state.day.emptyDay)
        assertEquals("16 ноября · Сегодня", state.day.dateText)
        assertFalse(state.isHistoryEmpty)
        val past = calculateProgress(workouts, today, TimeZone.UTC, LocalDate(2026, 11, 14)).toUiState()
        assertEquals(EmptyDayUiModel.Past, past.day.emptyDay)
        assertEquals("14 ноября", past.day.dateText)
        assertEquals("14", calculateProgress(listOf(workout(before, 14)), today, TimeZone.UTC).toUiState().averageRepetitions)
        assertEquals("1,7", calculateProgress(listOf(1, 2, 2).map { workout(before, it) }, today, TimeZone.UTC).toUiState().averageRepetitions)
    }

    private fun workout(day: LocalDate, repetitions: Int) = ProgressWorkout(day.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(), repetitions)
}
