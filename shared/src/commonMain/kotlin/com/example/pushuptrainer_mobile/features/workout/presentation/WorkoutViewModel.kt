package com.example.pushuptrainer_mobile.features.workout.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushuptrainer_mobile.core.navigation.NavigationEntry
import com.example.pushuptrainer_mobile.core.navigation.Router
import com.example.pushuptrainer_mobile.features.trainingresult.TrainingResultRoute
import com.example.pushuptrainer_mobile.features.workout.domain.model.CompletedWorkout
import com.example.pushuptrainer_mobile.features.workout.domain.model.WorkoutPhase
import com.example.pushuptrainer_mobile.features.workout.domain.usecase.SaveWorkoutUseCase
import com.example.pushuptrainer_mobile.features.workout.domain.usecase.WorkoutSessionUseCase
import com.example.pushuptrainer_mobile.ml.domain.model.PoseFrame
import com.example.pushuptrainer_mobile.ml.presentation.WorkoutCameraListener
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.savedstate.SavedState
import androidx.savedstate.serialization.encodeToSavedState
import androidx.savedstate.serialization.decodeFromSavedState

internal class WorkoutViewModel(
    private val entry: NavigationEntry,
    private val goal: Int?,
    private val savedStateHandle: SavedStateHandle,
    private val router: Router,
    private val session: WorkoutSessionUseCase,
    private val saveWorkout: SaveWorkoutUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(WorkoutUiState(
        repetitions = "0",
        mode = goal?.let { WorkoutModeUiModel.Goal(it.toString()) } ?: WorkoutModeUiModel.Free,
        hint = null,
        status = WorkoutStatus.Starting,
    ))
    val state = _state.asStateFlow()
    private var pending: CompletedWorkout? = null

    init {
        savedStateHandle.get<SavedState>(SNAPSHOT_KEY)?.let { encoded ->
            val restored = decodeFromSavedState<CompletedWorkout>(encoded)
            val finished = savedStateHandle.get<Boolean>(FINISHED_KEY) ?: false
            session.restore(restored, finished)
            if (finished) pending = restored
        }
        viewModelScope.launch {
            session.state.collect { value ->
                if (_state.value.status !in setOf(WorkoutStatus.Saving, WorkoutStatus.SaveError, WorkoutStatus.Saved)) {
                    val rendered = _state.value.withSession(value)
                    _state.update { current ->
                        if (pending == null && session.state.value == value) {
                            current.copy(
                                repetitions = rendered.repetitions,
                                status = rendered.status,
                                hint = rendered.hint,
                                cameraGeneration = rendered.cameraGeneration,
                            )
                        } else current
                    }
                }
                checkpoint()
                if (value.phase == WorkoutPhase.Finished && pending == null) finish()
            }
        }
        if (pending != null) save()
    }

    fun onEvent(event: WorkoutUiEvent) {
        when (event) {
            WorkoutUiEvent.FinishClicked -> finish()
            WorkoutUiEvent.BackClicked -> {
                if (pending == null) {
                    session.pause()
                    _state.update { it.copy(showFinishConfirmation = true) }
                }
            }
            WorkoutUiEvent.ConfirmFinishClicked -> finish()
            WorkoutUiEvent.DismissFinishClicked -> _state.update { it.copy(showFinishConfirmation = false) }
            WorkoutUiEvent.ResumeClicked -> session.resume()
            WorkoutUiEvent.RetrySaveClicked -> save()
            WorkoutUiEvent.ShowResultClicked -> showResult()
        }
    }

    fun cameraListener(generation: Int): WorkoutCameraListener = object : WorkoutCameraListener {
        override fun onReady() = session.ready(generation)
        override fun onError() = session.cameraFailed(generation)
        override fun onFrame(frame: PoseFrame) = session.onFrame(generation, frame)
    }

    fun onBackground() {
        session.pause()
        checkpoint()
    }

    fun onForeground() {
        if (_state.value.status == WorkoutStatus.Saved) showResult()
    }

    override fun onCleared() {
        session.pause()
    }

    private fun finish() {
        if (pending != null) return
        pending = session.finish(entry.id, goal)
        savedStateHandle[FINISHED_KEY] = true
        checkpoint()
        _state.update { it.copy(showFinishConfirmation = false) }
        save()
    }

    private fun checkpoint() {
        savedStateHandle[SNAPSHOT_KEY] = encodeToSavedState(pending ?: session.snapshot(entry.id, goal))
    }

    private fun save() {
        val result = pending ?: return
        if (_state.value.status == WorkoutStatus.Saving || _state.value.status == WorkoutStatus.Saved) return
        _state.update { it.copy(status = WorkoutStatus.Saving, repetitions = result.repetitions.toString()) }
        viewModelScope.launch {
            try {
                saveWorkout(result)
                _state.update { it.copy(status = WorkoutStatus.Saved) }
                showResult()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _state.update { it.copy(status = WorkoutStatus.SaveError) }
            }
        }
    }

    private fun showResult() {
        if (_state.value.status == WorkoutStatus.Saved) router.replace(entry, TrainingResultRoute(entry.id))
    }

    private companion object {
        const val SNAPSHOT_KEY = "workout.snapshot"
        const val FINISHED_KEY = "workout.finished"
    }
}
