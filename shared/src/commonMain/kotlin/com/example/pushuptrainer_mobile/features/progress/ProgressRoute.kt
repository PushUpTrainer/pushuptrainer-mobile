package com.example.pushuptrainer_mobile.features.progress

import androidx.navigation3.runtime.EntryProviderScope
import com.example.pushuptrainer_mobile.core.navigation.Destination
import com.example.pushuptrainer_mobile.features.progress.presentation.ProgressRoot
import kotlinx.serialization.Serializable

@Serializable
data object ProgressRoute : Destination

internal fun EntryProviderScope<Destination>.progressSection() {
    entry<ProgressRoute> { ProgressRoot() }
}
