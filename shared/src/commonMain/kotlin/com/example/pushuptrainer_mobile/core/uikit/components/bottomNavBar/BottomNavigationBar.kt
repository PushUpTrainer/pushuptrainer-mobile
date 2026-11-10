package com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme

/** Нижняя навигация: «Тренажёр», «Прогресс», «О проекте». */
@Composable
fun BottomNavigationBar(
    selected: BottomNavigationDestination,
    onDestinationClick: (BottomNavigationDestination) -> Unit,
    modifier: Modifier = Modifier,
    enabledDestinations: Set<BottomNavigationDestination> = BottomNavigationDestination.entries.toSet(),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
    ) {
        HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.outline)
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            BottomNavigationDestination.entries.forEach { destination ->
                BottomNavigationItem(
                    destination = destination,
                    isSelected = destination == selected,
                    enabled = destination in enabledDestinations,
                    onClick = { onDestinationClick(destination) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Preview
@Composable
private fun BottomNavigationBarLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            BottomNavigationBar(selected = BottomNavigationDestination.Trainer, onDestinationClick = {})
        }
    }
}

@Preview
@Composable
private fun BottomNavigationBarDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            BottomNavigationBar(selected = BottomNavigationDestination.Progress, onDestinationClick = {})
        }
    }
}
