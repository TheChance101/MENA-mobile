package net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.color_size
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ColorOptionsPlaceholder(
    backgroundColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorSize by animateDpAsState(
        targetValue = if (isSelected) 42.dp else 48.dp,
        label = stringResource(Res.string.color_size)
    )
    Box(
        modifier = modifier.size(48.dp)
            .clip(CircleShape)
            .background(Theme.colorScheme.background.surface)
            .border(1.dp, backgroundColor,CircleShape)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = MutableInteractionSource()
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(colorSize)
                .clip(CircleShape)
                .background(backgroundColor)
        )
    }
}

@Preview
@Composable
private fun DukanColorPreview() {
    MenaTheme {
        Column {
            ColorOptionsPlaceholder(
                backgroundColor = Theme.colorScheme.error,
                onClick = {},
                isSelected = true
            )

            ColorOptionsPlaceholder(
                backgroundColor = Theme.colorScheme.error,
                onClick = {},
                isSelected = false
            )
        }
    }
}
