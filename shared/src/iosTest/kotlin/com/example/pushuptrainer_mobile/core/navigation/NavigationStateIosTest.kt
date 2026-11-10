package com.example.pushuptrainer_mobile.core.navigation

import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import com.example.pushuptrainer_mobile.features.main.mainNavigationState
import com.example.pushuptrainer_mobile.features.trainingresult.TrainingResultRoute
import kotlin.test.Test
import kotlin.test.assertEquals

class NavigationStateIosTest {
    @Test
    fun restoresResultArgumentsAndEntryIdentityWithoutReflection() {
        val entry = TrainingResultRoute("saved-workout").newEntry()
        val encoded = encodeToSavedState(NavigationEntry.serializer(), entry, mainNavigationState)
        val decoded = decodeFromSavedState(NavigationEntry.serializer(), encoded, mainNavigationState)
        assertEquals(entry, decoded)
    }
}
