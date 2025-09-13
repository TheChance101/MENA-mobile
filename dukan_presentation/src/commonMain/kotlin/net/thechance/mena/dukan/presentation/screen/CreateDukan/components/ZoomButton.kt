package net.thechance.mena.dukan.presentation.screen.CreateDukan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun RoundIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = Theme.spacing._16,
    padding: Dp = 6.17.dp,
    backgroundColor: Color = Theme.colorScheme.background.surface,
    iconTint: Color = Theme.colorScheme.primary.primary,
    isEnabled: Boolean = true,
    disabledIconTint: Color = Theme.colorScheme.disabled
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                enabled = isEnabled,
                onClick = onClick
            )
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = if (isEnabled) iconTint else disabledIconTint,
            modifier = Modifier.size(iconSize)
        )
    }
}