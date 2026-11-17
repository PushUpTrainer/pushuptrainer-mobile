package com.example.pushuptrainer_mobile.features.about

import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.di.sharedModules
import com.example.pushuptrainer_mobile.features.about.presentation.AboutUiEffect
import com.example.pushuptrainer_mobile.features.about.presentation.AboutUiEvent
import com.example.pushuptrainer_mobile.features.about.presentation.AboutViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.koin.dsl.koinApplication

@OptIn(ExperimentalCoroutinesApi::class)
abstract class AboutViewModelTest {
    @Test
    fun repositoryLinkUsesProjectUrlAndIsNotReplayed() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val application = koinApplication { modules(sharedModules) }
        val viewModel = application.koin.get<AboutViewModel>()
        try {
            val received = mutableListOf<AboutUiEffect>()
            var collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.effects.collect { received += it }
            }
            viewModel.onEvent(AboutUiEvent.RepositoryLinkClicked)
            assertEquals(listOf<AboutUiEffect>(AboutUiEffect.OpenRepository("https://github.com/PushUpTrainer")), received)
            collector.cancel()
            received.clear()
            viewModel.onEvent(AboutUiEvent.RepositoryLinkClicked)
            collector = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
                viewModel.effects.collect { received += it }
            }
            assertTrue(received.isEmpty())
            collector.cancel()
        } finally {
            viewModel.viewModelScope.cancel()
            application.close()
            Dispatchers.resetMain()
        }
    }
}
