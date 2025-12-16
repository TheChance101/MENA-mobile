package net.thechance.mena.identity.presentation.screen.profile.imageCropper.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_add
import mena.identity_presentation.generated.resources.ic_remove
import mena.identity_presentation.generated.resources.reset
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ZoomOptionsSection(
    isZoomInEnabled: Boolean,
    isZoomOutEnabled: Boolean,
    onZoomInClicked: () -> Unit,
    onZoomOutClicked: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
    zoomInIcon: Painter = painterResource(Res.drawable.ic_add),
    zoomOutIcon: Painter = painterResource(Res.drawable.ic_remove),
    enabledColor: Color = Theme.colorScheme.primary.primary,
    disabledColor: Color = Theme.colorScheme.disabled
) {
    val zoomOutButtonBackground by animateColorAsState(
        targetValue = when (isZoomOutEnabled) {
            true -> Theme.colorScheme.background.surface
            false -> Theme.colorScheme.textDisabled
        },
        label = "zoomOutButtonBackground"
    )
    val zoomInButtonBackground by animateColorAsState(
        targetValue = when (isZoomInEnabled) {
            true -> Theme.colorScheme.background.surface
            false -> Theme.colorScheme.textDisabled
        },
        label = "zoomInButtonBackground"
    )

    val zoomInIconTint by animateColorAsState(
        targetValue = if (isZoomInEnabled) enabledColor else disabledColor
    )

    val zoomOutIconTint by animateColorAsState(
        targetValue = if (isZoomOutEnabled) enabledColor else disabledColor
    )

    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        ZoomButton(
            painter = zoomOutIcon,
            contentDescription = "zoom out",
            backgroundColor = zoomOutButtonBackground,
            iconTint = zoomOutIconTint,
            isEnabled = isZoomOutEnabled,
            onZoomClicked = onZoomOutClicked
        )

        ZoomButton(
            painter = zoomInIcon,
            contentDescription = "zoom in",
            backgroundColor = zoomInButtonBackground,
            iconTint = zoomInIconTint,
            isEnabled = isZoomInEnabled,
            onZoomClicked = onZoomInClicked,
            modifier = Modifier.padding(start = 4.dp)
        )

        Divider()

        TextButton(
            text = stringResource(Res.string.reset),
            onClick = onResetClick,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun ZoomButton(
    painter: Painter,
    contentDescription: String,
    backgroundColor: Color,
    iconTint: Color,
    onZoomClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = iconTint,
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.full))
            .clickable(
                onClick = onZoomClicked,
                enabled = isEnabled
            )
            .size(24.dp)
            .background(color = backgroundColor)
            .padding(4.dp)
    )
}

@Composable
private fun RowScope.Divider() {
    Box(
        modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(horizontal = 8.dp)
            .height(20.dp)
            .width(1.dp)
            .background(Theme.colorScheme.stroke)
    )
}

@Preview
@Composable
private fun ZoomOptionsSectionPreview() {
    MenaTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            ZoomOptionsSection(
                isZoomInEnabled = true,
                isZoomOutEnabled = true,
                onZoomInClicked = {},
                onZoomOutClicked = {},
                onResetClick = {}
            )
            ZoomOptionsSection(
                isZoomInEnabled = false,
                isZoomOutEnabled = true,
                onZoomInClicked = {},
                onZoomOutClicked = {},
                onResetClick = {}
            )
            ZoomOptionsSection(
                isZoomInEnabled = true,
                isZoomOutEnabled = false,
                onZoomInClicked = {},
                onZoomOutClicked = {},
                onResetClick = {}
            )
        }
    }
}
