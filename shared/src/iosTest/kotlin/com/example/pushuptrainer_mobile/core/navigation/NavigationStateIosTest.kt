package com.example.pushuptrainer_mobile.core.navigation

import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import com.example.pushuptrainer_mobile.features.about.AboutRoute
import com.example.pushuptrainer_mobile.features.main.mainNavigationState
import com.example.pushuptrainer_mobile.features.trainingresult.TrainingResultRoute
import com.example.pushuptrainer_mobile.features.workout.WorkoutRoute
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationStateIosTest {
    @Test
    fun restoresAboutEntryWithoutReflection() {
        val entry = AboutRoute.newEntry()
        val encoded = encodeToSavedState(NavigationEntry.serializer(), entry, mainNavigationState)
        assertEquals(entry, decodeFromSavedState(NavigationEntry.serializer(), encoded, mainNavigationState))
    }

    @Test
    fun restoresWorkoutGoalAndFreeModeWithEntryIdentity() {
        for (goal in listOf(null, 1, 100)) {
            val entry = WorkoutRoute(goal).newEntry()
            val encoded = encodeToSavedState(NavigationEntry.serializer(), entry, mainNavigationState)
            assertEquals(entry, decodeFromSavedState(NavigationEntry.serializer(), encoded, mainNavigationState))
        }
    }

    @Test
    fun restoresResultArgumentsAndEntryIdentityWithoutReflection() {
        val entry = TrainingResultRoute("saved-workout").newEntry()
        val encoded = encodeToSavedState(NavigationEntry.serializer(), entry, mainNavigationState)
        val decoded = decodeFromSavedState(NavigationEntry.serializer(), encoded, mainNavigationState)
        assertEquals(entry, decoded)
    }
}
