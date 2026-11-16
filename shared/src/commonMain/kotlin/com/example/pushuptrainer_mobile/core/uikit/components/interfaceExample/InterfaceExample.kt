package com.example.pushuptrainer_mobile.core.uikit.components.interfaceExample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationMode
import com.example.pushuptrainer_mobile.core.uikit.components.preparationModeSwitch.PreparationModeSwitch
import com.example.pushuptrainer_mobile.core.uikit.components.themeToggleButton.ThemeToggleButton
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme

/**
 * Пример интерфейса приложения, который показывается как картинка (например, в онбординге).
 *
 * Внутри можно переиспользовать настоящие компоненты UI kit, но они становятся некликабельными:
 * - поверх содержимого лежит прозрачный слой, который первым принимает касания,
 *   поэтому кнопки под ним не нажимаются и не показывают эффект нажатия;
 * - слой касания не поглощает, поэтому родительские жесты (свайп pager-а, прокрутка) работают
 *   и поверх примера;
 * - кнопки внутри убраны из дерева доступности, чтобы экранный диктор не предлагал их нажать.
 *
 * Внешний вид компонентов не меняется: в отличие от `enabled = false`, они не тускнеют.
 */
@Composable
fun InterfaceExample(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.clearAndSetSemantics {}) {
        content()
        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    // Только принимаем касание, ничего не потребляя (consume не вызываем).
                    awaitPointerEventScope {
                        while (true) awaitPointerEvent()
                    }
                },
        )
    }
}

@Preview
@Composable
private fun InterfaceExampleLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        InterfaceExamplePreviewContent()
    }
}

@Preview
@Composable
private fun InterfaceExampleDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        InterfaceExamplePreviewContent()
    }
}

@Composable
private fun InterfaceExamplePreviewContent() {
    Surface(color = AppTheme.colors.background) {
        InterfaceExample(modifier = Modifier.padding(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ThemeToggleButton(isDarkTheme = AppTheme.isDarkTheme, onClick = {})
                PreparationModeSwitch(selectedMode = PreparationMode.WithGoal, onModeSelect = {})
            }
        }
    }
}
