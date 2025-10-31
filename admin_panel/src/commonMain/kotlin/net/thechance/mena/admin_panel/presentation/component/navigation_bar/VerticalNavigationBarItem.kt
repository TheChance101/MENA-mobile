package net.thechance.mena.admin_panel.presentation.component.navigation_bar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun VerticalNavigationBarItem(
    isSelected: Boolean,
    unselectedIcon: Painter,
    selectedIcon: Painter,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val painter = if (isSelected) selectedIcon else unselectedIcon
    val interactionSource = remember { MutableInteractionSource() }
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            Theme.colorScheme.shadeSecondary

        } else {
            Theme.colorScheme.brand.brand
        },
        animationSpec = tween(durationMillis = 300),
        label = "textColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .width(112.dp)
            .clickableItem(
                isSelected = isSelected,
                interactionSource = interactionSource,
                onClick = onClick
            )
    ) {
        Icon(
            painter = painter,
            modifier = Modifier.size(32.dp),
            contentDescription = title,
        )
        Text(
            text = title,
            style = Theme.typography.label.small,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )
    }
}

private fun Modifier.clickableItem(
    isSelected: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
): Modifier = then(
    if (isSelected) {
        Modifier
    } else {
        Modifier.clickable(
            onClick = onClick,
            indication = null,
            interactionSource = interactionSource
        )
    }
)