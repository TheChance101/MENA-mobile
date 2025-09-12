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
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ZoomControls(
    onZoomInClicked: () -> Unit,
    onZoomOutClicked: () -> Unit,
    onResetClicked: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.colorScheme.background.surfaceLow
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(backgroundColor)
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        RoundIconButton(
//            icon = Res.drawable,//Add the Icon Here
//            contentDescription = "Zoom In",
//            onClick = onZoomInClicked,
//            iconTint = Color.Black
//        )
//        RoundIconButton(
//            icon = Res.drawable.,//Add the Icon Here
//            contentDescription = "Zoom Out",
//            onClick = onZoomOutClicked,
//            iconTint = Color.Black
//        )
        TextButton(
            onClick = onResetClicked,
            colors = ButtonDefaults.textButtonColors(
                contentColor = Theme.colorScheme.primary.primary
            )
        ) {
            Text(
                text = "Reset",
                style = Theme.typography.label.medium,
            )
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