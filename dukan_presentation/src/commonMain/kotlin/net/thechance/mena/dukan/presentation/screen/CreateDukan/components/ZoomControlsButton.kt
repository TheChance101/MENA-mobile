package net.thechance.mena.dukan.presentation.screen.CreateDukan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_add
import mena.dukan_presentation.generated.resources.ic_remove
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ZoomControls(
    onZoomInClicked: () -> Unit,
    onZoomOutClicked: () -> Unit,
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.colorScheme.background.surfaceLow,
    isZoomOutEnabled: Boolean = true
) {

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.full))
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
            iconTint = if (isZoomOutEnabled)
                Theme.colorScheme.primary.primary
            else
                Theme.colorScheme.disabled
        )

        RoundIconButton(
            icon = painterResource(Res.drawable.ic_add),
            contentDescription = "Zoom In",
            onClick = onZoomInClicked
        )

        TextButton(
            onClick = onResetClicked,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Theme.colorScheme.primary.primary
            )
        ) {
            Text("Reset", style = Theme.typography.label.medium)
        }
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