package app.pushups.presentation.workout

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.pushups.contract.Hint
import app.pushups.contract.PoseFrame
import app.pushups.contract.TechniqueError
import app.pushups.logic.PushupCounter

data class WorkoutStatusUiModel(
    val repsText: String,
    val hintText: String,
    val errorsText: String,
)

data class WorkoutUiState(
    val status: WorkoutStatusUiModel = WorkoutStatusUiModel(
        repsText = "Отжимания: 0",
        hintText = "Ожидание камеры",
        errorsText = "",
    ),
)

sealed interface WorkoutUiEvent {
    data object ResetCounter : WorkoutUiEvent
}

class WorkoutViewModel {
    private var counter = PushupCounter()

    var state by mutableStateOf(WorkoutUiState())
        private set

    fun onEvent(event: WorkoutUiEvent) {
        when (event) {
            WorkoutUiEvent.ResetCounter -> {
                counter = PushupCounter()
                state = WorkoutUiState()
            }
        }
    }

    fun onFrame(frame: PoseFrame) {
        val result = counter.onFrame(frame)
        state = WorkoutUiState(
            status = WorkoutStatusUiModel(
                repsText = "Отжимания: ${result.reps}",
                hintText = when (result.hint) {
                    Hint.NO_PERSON -> "Встаньте в кадр"
                    Hint.ARMS_HIDDEN -> "Не видно плеч или рук"
                    Hint.TOO_FAR -> "Слишком далеко от камеры"
                    Hint.TOO_CLOSE -> "Слишком близко к камере"
                    null -> ""
                },
                errorsText = result.errors.joinToString("\n") {
                    when (it) {
                        TechniqueError.HANDS_TOO_WIDE -> "Руки слишком широко"
                        TechniqueError.HANDS_TOO_NARROW -> "Руки слишком узко"
                        TechniqueError.SHALLOW_REP -> "Неглубокий повтор"
                    }
                },
            ),
        )
    }

    fun onCameraError(message: String) {
        state = state.copy(status = state.status.copy(hintText = message))
    }
}
