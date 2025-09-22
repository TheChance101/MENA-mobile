package net.thechance.mena.dukan.presentation.screen.cropImage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun RoundIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    iconSize: Dp = 16.dp,
    padding: Dp = Theme.spacing._4,
    backgroundColor: Color = Theme.colorScheme.background.surface,
    iconTint: Color = Theme.colorScheme.primary.primary,
    isEnabled: Boolean = true,
    disabledIconTint: Color = Theme.colorScheme.disabled,
    modifier: Modifier = Modifier,
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
            modifier = modifier.size(iconSize)
        )
    }
}

@Preview
@Composable
private fun RoundIconButtonPreview() {
    MenaTheme {
        RoundIconButton(
            icon = painterResource(Res.drawable.ic_add),
            contentDescription = "Zoom in",
            onClick = {}
        )
    }
}