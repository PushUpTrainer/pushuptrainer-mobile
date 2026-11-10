package com.example.pushuptrainer_mobile.core.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

interface Destination : NavKey

@Serializable
data class NavigationEntry internal constructor(
    val id: String,
    @Polymorphic val destination: Destination,
) : NavKey

@OptIn(ExperimentalUuidApi::class)
internal fun Destination.newEntry() = NavigationEntry(Uuid.random().toString(), this)

internal fun navigationStateConfiguration(
    destinations: PolymorphicModuleBuilder<Destination>.() -> Unit,
) = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(NavigationEntry::class, NavigationEntry.serializer())
        }
        polymorphic(Destination::class, builderAction = destinations)
    }
}
