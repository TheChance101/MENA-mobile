package net.thechance.mena.dukan.presentation.screen.cropImage.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add
import mena.dukan_presentation.generated.resources.ic_remove
import mena.dukan_presentation.generated.resources.reset
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ZoomControls(
    onZoomInClicked: () -> Unit,
    onZoomOutClicked: () -> Unit,
    onResetClicked: () -> Unit,
    backgroundColor: Color = Theme.colorScheme.background.surfaceLow,
    isZoomOutEnabled: Boolean = true,
    isZoomInEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val zoomInButtonIconTint = animateColorAsState(
        targetValue = if (isZoomInEnabled) Theme.colorScheme.primary.primary else Theme.colorScheme.disabled,
        label = "ZoomOutIconTint"
    ).value

    val zoomOutButtonIconTint = animateColorAsState(
        targetValue = if (isZoomOutEnabled) Theme.colorScheme.primary.primary else Theme.colorScheme.disabled,
        label = "ZoomOutIconTint"
    ).value

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = Theme.spacing._12, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundIconButton(
            icon = painterResource(Res.drawable.ic_remove),
            contentDescription = "Zoom Out",
            onClick = onZoomOutClicked,
            isEnabled = isZoomOutEnabled,
            iconTint = zoomOutButtonIconTint
        )

        RoundIconButton(
            icon = painterResource(Res.drawable.ic_add),
            contentDescription = "Zoom In",
            onClick = onZoomInClicked,
            isEnabled = isZoomInEnabled,
            iconTint = zoomInButtonIconTint
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .background(Theme.colorScheme.stroke)
        )

        TextButton(
            text = stringResource(Res.string.reset),
            onClick = onResetClicked,
        )
    }
}

@Preview
@Composable
private fun ZoomControlsPreview() {
    MenaTheme {
        ZoomControls(
            onZoomInClicked = {},
            onZoomOutClicked = {},
            onResetClicked = {}
        )
    }
}