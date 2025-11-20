package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_add
import mena.faith_presentation.generated.resources.ic_remove
import mena.faith_presentation.generated.resources.reset
import net.thechance.mena.designsystem.presentation.component.button.TextButton
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ZoomControls(
    onZoomInClicked: () -> Unit,
    onZoomOutClicked: () -> Unit,
    onResetClicked: () -> Unit,
    isZoomOutEnabled: Boolean = true,
    isZoomInEnabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(horizontal = Theme.spacing._12, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundIconButton(
            icon = painterResource(Res.drawable.ic_remove),
            contentDescription = "Zoom Out",
            onClick = onZoomOutClicked,
            isEnabled = isZoomOutEnabled,
        )

        RoundIconButton(
            icon = painterResource(Res.drawable.ic_add),
            contentDescription = "Zoom In",
            onClick = onZoomInClicked,
            isEnabled = isZoomInEnabled,
        )

        VerticalDivider(
            thickness = 0.5.dp,
            color = Theme.colorScheme.stroke
        )

        TextButton(
            text = stringResource(Res.string.reset),
            onClick = onResetClicked,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            ZoomControls(
                onZoomInClicked = {},
                onZoomOutClicked = {},
                onResetClicked = {}
            )
        }
    }
}
