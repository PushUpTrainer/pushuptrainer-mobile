package app.pushups.presentation.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

expect class CameraPreviewHandle

@Composable
expect fun CameraPreview(handle: CameraPreviewHandle, modifier: Modifier = Modifier)

@Composable
internal fun CameraPreviewPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier.background(Color.DarkGray), contentAlignment = Alignment.Center) {
        Text("Предпросмотр камеры", color = Color.White)
    }
}

@Preview
@Composable
private fun CameraPreviewPreview() {
    MaterialTheme { CameraPreviewPlaceholder(Modifier.fillMaxSize()) }
}
