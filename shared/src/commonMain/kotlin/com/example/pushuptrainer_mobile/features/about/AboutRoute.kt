package com.example.pushuptrainer_mobile.features.about

import androidx.navigation3.runtime.EntryProviderScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.features.about.presentation.AboutRoute as AboutRoot
import kotlinx.serialization.Serializable

@Serializable
internal data object AboutRoute : Destination

internal fun EntryProviderScope<Destination>.aboutSection() {
    entry<AboutRoute> { AboutRoot() }
}
