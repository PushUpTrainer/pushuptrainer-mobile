package com.example.pushuptrainer_mobile.features.workout.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.painterResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.workout_camera_sample

@Preview(widthDp = 360, heightDp = 800)
@Composable
fun WorkoutFreeDarkPreview() = WorkoutPreview(darkTheme = true, withGoal = false)

@Preview(widthDp = 360, heightDp = 800)
@Composable
fun WorkoutFreeLightPreview() = WorkoutPreview(darkTheme = false, withGoal = false)

@Preview(widthDp = 360, heightDp = 800)
@Composable
fun WorkoutGoalDarkPreview() = WorkoutPreview(darkTheme = true, withGoal = true)

@Preview(widthDp = 360, heightDp = 800)
@Composable
fun WorkoutGoalLightPreview() = WorkoutPreview(darkTheme = false, withGoal = true)

@Preview(widthDp = 320, heightDp = 480, fontScale = 1.5f)
@Composable
fun WorkoutCompactPreview() = WorkoutPreview(darkTheme = true, withGoal = true)

@Composable
private fun WorkoutPreview(darkTheme: Boolean, withGoal: Boolean) {
    PushUpTrainerTheme(darkTheme = darkTheme) {
        WorkoutScreen(
            state = WorkoutUiState(
                counter = if (withGoal) WorkoutCounterUiModel.Goal("16", "20") else WorkoutCounterUiModel.Free("16"),
                hint = WorkoutHintUiModel("Тело в кадре", "Подсказки по технике"),
            ),
            onEvent = {},
        ) {
            Image(
                painter = painterResource(Res.drawable.workout_camera_sample),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
