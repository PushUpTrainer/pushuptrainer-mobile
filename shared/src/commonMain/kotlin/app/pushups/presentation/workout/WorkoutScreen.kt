package app.pushups.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WorkoutScreen(
    state: WorkoutUiState,
    onEvent: (WorkoutUiEvent) -> Unit,
    cameraPreview: @Composable () -> Unit,
) {
    MaterialTheme {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            cameraPreview()
            WorkoutStatus(
                model = state.status,
                onReset = { onEvent(WorkoutUiEvent.ResetCounter) },
                modifier = Modifier.align(Alignment.TopCenter).windowInsetsPadding(WindowInsets.safeDrawing).padding(top = 20.dp),
            )
        }
    }
}

@Composable
private fun WorkoutStatus(
    model: WorkoutStatusUiModel,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(model.repsText, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
        if (model.hintText.isNotEmpty()) Text(model.hintText, color = Color.White)
        if (model.errorsText.isNotEmpty()) Text(model.errorsText, color = Color.White)
        Button(onClick = onReset) { Text("Сбросить счётчик") }
    }
}

@Preview
@Composable
private fun WorkoutStatusPreview() {
    MaterialTheme {
        WorkoutStatus(
            model = WorkoutStatusUiModel("Отжимания: 5", "Встаньте в кадр", ""),
            onReset = {},
        )
    }
}

@Preview
@Composable
private fun WorkoutScreenPreview() {
    WorkoutScreen(
        state = WorkoutUiState(WorkoutStatusUiModel("Отжимания: 5", "Встаньте в кадр", "")),
        onEvent = {},
        cameraPreview = { CameraPreviewPlaceholder(Modifier.fillMaxSize()) },
    )
}
