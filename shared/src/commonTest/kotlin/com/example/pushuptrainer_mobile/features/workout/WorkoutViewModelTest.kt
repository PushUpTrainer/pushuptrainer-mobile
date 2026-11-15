package com.example.pushuptrainer_mobile.features.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.StackRouter
import com.example.pushuptrainer_mobile.core.navigation.newEntry
import com.example.pushuptrainer_mobile.features.home.HomeRoute
import com.example.pushuptrainer_mobile.features.preparation.PreparationRoute
import com.example.pushuptrainer_mobile.features.trainingresult.TrainingResultRoute
import com.example.pushuptrainer_mobile.features.workout.domain.model.CompletedWorkout
import com.example.pushuptrainer_mobile.features.workout.domain.repository.WorkoutRepository
import com.example.pushuptrainer_mobile.features.workout.domain.usecase.SaveWorkoutUseCase
import com.example.pushuptrainer_mobile.features.workout.domain.usecase.WorkoutSessionUseCase
import com.example.pushuptrainer_mobile.features.workout.presentation.WorkoutStatus
import com.example.pushuptrainer_mobile.features.workout.presentation.WorkoutUiEvent
import com.example.pushuptrainer_mobile.features.workout.presentation.WorkoutViewModel
import com.example.pushuptrainer_mobile.di.sharedModules
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.TestScope

@OptIn(ExperimentalCoroutinesApi::class)
abstract class WorkoutViewModelTest {
    @Test
    fun sharedGraphCreatesIndependentGoalAndFreeWorkoutEntries() = runTest {
        fixture {
            val application = koinApplication {
                modules(sharedModules, module {
                    factory { SavedStateHandle() }
                    single<WorkoutRepository> { repository }
                })
            }
            val first = application.koin.get<WorkoutViewModel> { parametersOf(WorkoutRoute(5).newEntry(), 5) }
            val second = application.koin.get<WorkoutViewModel> { parametersOf(WorkoutRoute(null).newEntry(), null) }
            try {
                runCurrent()
                assertEquals(com.example.pushuptrainer_mobile.features.workout.presentation.WorkoutModeUiModel.Goal("5"), first.state.value.mode)
                assertEquals(com.example.pushuptrainer_mobile.features.workout.presentation.WorkoutModeUiModel.Free, second.state.value.mode)
                first.onBackground()
                runCurrent()
                assertEquals(WorkoutStatus.Paused, first.state.value.status)
                assertEquals(WorkoutStatus.Starting, second.state.value.status)
            } finally {
                first.viewModelScope.cancel()
                second.viewModelScope.cancel()
                application.close()
            }
        }
    }

    @Test
    fun unfinishedSnapshotRestoresPausedAndContinuesWithoutLosingReps() = runTest {
        fixture {
            vm.cameraListener(0).onReady()
            vm.cameraListener(0).onFrame(frame(1))
            vm.onBackground()
            runCurrent()
            val restored = createViewModel(SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) }))
            try {
                runCurrent()
                assertEquals(WorkoutStatus.Paused, restored.state.value.status)
                assertEquals("1", restored.state.value.repetitions)
                restored.onEvent(WorkoutUiEvent.ResumeClicked)
                restored.cameraListener(1).onReady()
                restored.cameraListener(1).onFrame(frame(2))
                restored.onEvent(WorkoutUiEvent.FinishClicked)
                runCurrent()
                assertEquals(2, repository.saved.single().repetitions)
            } finally {
                restored.viewModelScope.cancel()
            }
        }
    }

    @Test
    fun goalAndRepeatedFinishSaveOneFrozenResultAndReplaceWorkoutEntry() = runTest {
        fixture(goal = 2) {
            val listener = vm.cameraListener(0)
            listener.onReady()
            repeat(5) { listener.onFrame(frame(it.toLong())) }
            repeat(3) { vm.onEvent(WorkoutUiEvent.FinishClicked) }
            runCurrent()
            assertEquals(1, repository.attempts)
            val result = repository.saved.single()
            assertEquals(2, result.repetitions)
            assertEquals(2, result.goal)
            assertEquals(TrainingResultRoute(entry.id), (stack.last() as NavigationEntry).destination)
            assertEquals(3, stack.size)
            listener.onFrame(frame(50))
            runCurrent()
            assertEquals(listOf(result), repository.saved)
        }
    }

    @Test
    fun failedSaveRetriesSameSnapshotAndBackgroundDefersNavigation() = runTest {
        fixture {
            repository.fail = true
            vm.cameraListener(0).onReady()
            vm.cameraListener(0).onFrame(frame(1))
            vm.onEvent(WorkoutUiEvent.FinishClicked)
            runCurrent()
            assertEquals(WorkoutStatus.SaveError, vm.state.value.status)
            assertEquals(entry, stack.last())
            repository.fail = false
            active = false
            vm.onEvent(WorkoutUiEvent.RetrySaveClicked)
            runCurrent()
            assertEquals(WorkoutStatus.Saved, vm.state.value.status)
            assertEquals(entry, stack.last())
            active = true
            vm.onForeground()
            assertEquals(TrainingResultRoute(entry.id), (stack.last() as NavigationEntry).destination)
            assertEquals(1, repository.saved.single().repetitions)
        }
    }

    @Test
    fun backPausesAndDismissKeepsResultWithoutSaving() = runTest {
        fixture {
            vm.cameraListener(0).onReady()
            vm.cameraListener(0).onFrame(frame(1))
            runCurrent()
            vm.onEvent(WorkoutUiEvent.BackClicked)
            runCurrent()
            assertTrue(vm.state.value.showFinishConfirmation)
            assertEquals(WorkoutStatus.Paused, vm.state.value.status)
            vm.onEvent(WorkoutUiEvent.DismissFinishClicked)
            runCurrent()
            assertFalse(vm.state.value.showFinishConfirmation)
            assertEquals(0, repository.attempts)
            vm.onEvent(WorkoutUiEvent.ResumeClicked)
            runCurrent()
            vm.cameraListener(1).onReady()
            vm.cameraListener(0).onFrame(frame(2))
            vm.cameraListener(1).onFrame(frame(3))
            runCurrent()
            assertEquals("2", vm.state.value.repetitions)
        }
    }

    @Test
    fun restoredFinishedEntryRetriesSameIdWithoutRestartingCamera() = runTest {
        fixture {
            repository.gate = CompletableDeferred()
            vm.cameraListener(0).onReady()
            vm.cameraListener(0).onFrame(frame(1))
            vm.onEvent(WorkoutUiEvent.FinishClicked)
            runCurrent()
            vm.viewModelScope.cancel()
            repository.gate = null
            val restored = createViewModel(SavedStateHandle(handle.keys().associateWith { handle.get<Any?>(it) }))
            try {
                runCurrent()
                assertEquals(WorkoutStatus.Saved, restored.state.value.status)
                assertEquals(entry.id, repository.saved.single().id)
                assertEquals(1, repository.saved.single().repetitions)
            } finally {
                restored.viewModelScope.cancel()
            }
        }
    }

    private suspend fun TestScope.fixture(goal: Int? = null, body: suspend Fixture.() -> Unit) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val fixture = Fixture(goal)
        try {
            runCurrent()
            fixture.body()
        } finally {
            fixture.vm.viewModelScope.cancel()
            Dispatchers.resetMain()
        }
    }

    private class Fixture(val goal: Int?) {
        val entry = WorkoutRoute(goal).newEntry()
        val stack = mutableListOf<NavKey>(HomeRoute.newEntry(), PreparationRoute.newEntry(), entry)
        var active = true
        val router = StackRouter().also { it.attach(stack) { active } }
        val repository = RecordingRepository()
        val handle = SavedStateHandle()
        val vm = createViewModel(handle)
        fun createViewModel(handle: SavedStateHandle) = WorkoutViewModel(
            entry, goal, handle, router,
            WorkoutSessionUseCase(goal = goal, analyzerFactory = { CountingAnalyzer() }),
            SaveWorkoutUseCase(repository),
        )
    }

    private class RecordingRepository : WorkoutRepository {
        var attempts = 0
        var fail = false
        var gate: CompletableDeferred<Unit>? = null
        val saved = mutableListOf<CompletedWorkout>()
        override suspend fun save(workout: CompletedWorkout) {
            attempts++
            gate?.await()
            if (fail) error("storage unavailable")
            saved.add(workout)
        }
    }
}
