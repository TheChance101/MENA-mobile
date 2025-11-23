package net.thechance.mena.admin_panel.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.ic_dukan_location
import net.thechance.mena.admin_panel.resources.location
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DukanLocation(
    location: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 16.dp,
    iconTint: Color = Theme.colorScheme.shadeSecondary,
    textStyle: TextStyle = Theme.typography.label.small,
    textColor: Color = Theme.colorScheme.shadeSecondary,
    spacing: Dp = 4.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_dukan_location),
            contentDescription = stringResource(Res.string.location),
            tint = iconTint,
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = location,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            style = textStyle,
            color = textColor
        )
    }
}