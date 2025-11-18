package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_add
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun RoundIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val iconTint = animateColorAsState(
        targetValue = if (isEnabled) Theme.colorScheme.primary.primary else Theme.colorScheme.disabled,
        label = "IconTint"
    ).value
    val backgroundColor = animateColorAsState(
        targetValue = if (isEnabled) Theme.colorScheme.background.surface else Theme.colorScheme.textDisabled,
        label = "BackgroundColor"
    ).value

    Icon(
        painter = icon,
        contentDescription = contentDescription,
        tint = iconTint,
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                enabled = isEnabled,
                onClick = onClick
            )
            .padding(Theme.spacing._4),
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            RoundIconButton(
                icon = painterResource(Res.drawable.ic_add),
                contentDescription = "Zoom in",
                onClick = {}
            )
        }
    }
}
