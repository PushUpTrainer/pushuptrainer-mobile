package com.example.pushuptrainer_mobile.core.uikit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
import com.example.pushuptrainer_mobile.core.uikit.theme.PushUpTrainerTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pushuptrainermobile.shared.generated.resources.Res
import pushuptrainermobile.shared.generated.resources.bottom_bar_about
import pushuptrainermobile.shared.generated.resources.bottom_bar_progress
import pushuptrainermobile.shared.generated.resources.bottom_bar_trainer
import pushuptrainermobile.shared.generated.resources.ic_nav_about
import pushuptrainermobile.shared.generated.resources.ic_nav_progress
import pushuptrainermobile.shared.generated.resources.ic_nav_trainer

/** Разделы нижней навигации. */
enum class AppBottomBarDestination {
    Trainer,
    Progress,
    About,
}

/** Нижняя навигация: «Тренажёр», «Прогресс», «О проекте». */
@Composable
fun AppBottomBar(
    selected: AppBottomBarDestination,
    onDestinationClick: (AppBottomBarDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
    ) {
        HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.outline)
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
            AppBottomBarDestination.entries.forEach { destination ->
                AppBottomBarItem(
                    destination = destination,
                    isSelected = destination == selected,
                    onClick = { onDestinationClick(destination) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AppBottomBarItem(
    destination: AppBottomBarDestination,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = if (isSelected) AppTheme.colors.accent else AppTheme.colors.textSecondary
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(top = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(destination.icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(destination.label),
            style = AppTheme.typography.navLabel,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

private val AppBottomBarDestination.label: StringResource
    get() = when (this) {
        AppBottomBarDestination.Trainer -> Res.string.bottom_bar_trainer
        AppBottomBarDestination.Progress -> Res.string.bottom_bar_progress
        AppBottomBarDestination.About -> Res.string.bottom_bar_about
    }

private val AppBottomBarDestination.icon: DrawableResource
    get() = when (this) {
        AppBottomBarDestination.Trainer -> Res.drawable.ic_nav_trainer
        AppBottomBarDestination.Progress -> Res.drawable.ic_nav_progress
        AppBottomBarDestination.About -> Res.drawable.ic_nav_about
    }

@Preview
@Composable
private fun AppBottomBarLightPreview() {
    PushUpTrainerTheme(darkTheme = false) {
        Surface(color = AppTheme.colors.background) {
            AppBottomBar(selected = AppBottomBarDestination.Trainer, onDestinationClick = {})
        }
    }
}

@Preview
@Composable
private fun AppBottomBarDarkPreview() {
    PushUpTrainerTheme(darkTheme = true) {
        Surface(color = AppTheme.colors.background) {
            AppBottomBar(selected = AppBottomBarDestination.Progress, onDestinationClick = {})
        }
    }
}
