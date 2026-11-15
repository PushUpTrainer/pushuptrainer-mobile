package com.example.pushuptrainer_mobile.features.workout.presentation

import com.example.pushuptrainer_mobile.core.uikit.components.workoutHint.WorkoutHintUiModel
import com.example.pushuptrainer_mobile.features.workout.domain.model.WorkoutPhase
import com.example.pushuptrainer_mobile.features.workout.domain.model.WorkoutSessionState
import com.example.pushuptrainer_mobile.ml.domain.model.Hint
import com.example.pushuptrainer_mobile.ml.domain.model.TechniqueError
import org.jetbrains.compose.resources.getString
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.workout_no_person_title
import pushuptrainermobile.shared.generated.resources.workout_no_person_body
import pushuptrainermobile.shared.generated.resources.workout_arms_title
import pushuptrainermobile.shared.generated.resources.workout_arms_body
import pushuptrainermobile.shared.generated.resources.workout_far_title
import pushuptrainermobile.shared.generated.resources.workout_far_body
import pushuptrainermobile.shared.generated.resources.workout_close_title
import pushuptrainermobile.shared.generated.resources.workout_close_body
import pushuptrainermobile.shared.generated.resources.workout_wide_title
import pushuptrainermobile.shared.generated.resources.workout_wide_body
import pushuptrainermobile.shared.generated.resources.workout_narrow_title
import pushuptrainermobile.shared.generated.resources.workout_narrow_body

internal suspend fun WorkoutUiState.withSession(session: WorkoutSessionState): WorkoutUiState = copy(
    repetitions = session.analysis?.reps?.toString() ?: repetitions,
    cameraGeneration = session.generation,
    status = when (session.phase) {
        WorkoutPhase.Starting -> WorkoutStatus.Starting
        WorkoutPhase.Running -> WorkoutStatus.Running
        WorkoutPhase.Paused -> WorkoutStatus.Paused
        WorkoutPhase.CameraError -> WorkoutStatus.CameraError
        WorkoutPhase.Finished -> status
    },
    hint = when (session.analysis?.hint) {
        Hint.NO_PERSON -> WorkoutHintUiModel(getString(Res.string.workout_no_person_title), getString(Res.string.workout_no_person_body))
        Hint.ARMS_HIDDEN -> WorkoutHintUiModel(getString(Res.string.workout_arms_title), getString(Res.string.workout_arms_body))
        Hint.TOO_FAR -> WorkoutHintUiModel(getString(Res.string.workout_far_title), getString(Res.string.workout_far_body))
        Hint.TOO_CLOSE -> WorkoutHintUiModel(getString(Res.string.workout_close_title), getString(Res.string.workout_close_body))
        null -> when {
            session.analysis?.errors?.contains(TechniqueError.HANDS_TOO_WIDE) == true ->
                WorkoutHintUiModel(getString(Res.string.workout_wide_title), getString(Res.string.workout_wide_body))
            session.analysis?.errors?.contains(TechniqueError.HANDS_TOO_NARROW) == true ->
                WorkoutHintUiModel(getString(Res.string.workout_narrow_title), getString(Res.string.workout_narrow_body))
            else -> null
        }
    },
)
