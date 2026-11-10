package com.example.pushuptrainer_mobile.core.uikit.components.bottomNavBar

import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pushuptrainer_mobile.core.uikit.theme.AppTheme
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

/** Пункт нижней навигации: иконка и подпись. Выбранный пункт подсвечен акцентным цветом. */
@Composable
internal fun BottomNavigationItem(
    destination: BottomNavigationDestination,
    isSelected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = when {
        !enabled -> AppTheme.colors.textSecondary.copy(alpha = 0.55f)
        isSelected -> AppTheme.colors.accent
        else -> AppTheme.colors.textSecondary
    }
    Column(
        modifier = modifier
            .fillMaxHeight()
            .selectable(selected = isSelected, enabled = enabled, role = Role.Tab, onClick = onClick)
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

private val BottomNavigationDestination.label: StringResource
    get() = when (this) {
        BottomNavigationDestination.Trainer -> Res.string.bottom_bar_trainer
        BottomNavigationDestination.Progress -> Res.string.bottom_bar_progress
        BottomNavigationDestination.About -> Res.string.bottom_bar_about
    }

private val BottomNavigationDestination.icon: DrawableResource
    get() = when (this) {
        BottomNavigationDestination.Trainer -> Res.drawable.ic_nav_trainer
        BottomNavigationDestination.Progress -> Res.drawable.ic_nav_progress
        BottomNavigationDestination.About -> Res.drawable.ic_nav_about
    }
