package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.resources.Res
import net.thechance.mena.admin_panel.resources.activate
import net.thechance.mena.admin_panel.resources.active
import net.thechance.mena.admin_panel.resources.block
import net.thechance.mena.admin_panel.resources.blocked
import net.thechance.mena.admin_panel.resources.ic_activate
import net.thechance.mena.admin_panel.resources.ic_block
import net.thechance.mena.designsystem.presentation.component.button.OutlinedButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserStatusToggleButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val buttonText = if (isActive) {
        stringResource(Res.string.block)
    } else {
        stringResource(Res.string.activate)
    }
    val buttonTrailingIcon = if (isActive) {
        painterResource(Res.drawable.ic_block)
    } else {
        painterResource(Res.drawable.ic_activate)
    }

    OutlinedButton(
        text = buttonText,
        trailingIcon = buttonTrailingIcon,
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
        modifier = modifier.wrapContentWidth(),
    )
}

@Composable
fun UserStatusButton(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (isActive) Theme.colorScheme.background.bgSuccess
        else Theme.colorScheme.background.bgError

    val contentColor = if (isActive) Theme.colorScheme.success else Theme.colorScheme.error

    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "statusBackgroundColor"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = contentColor,
        animationSpec = tween(durationMillis = 300),
        label = "statusDotColor"
    )

    val statusText = if (isActive) {
        stringResource(Res.string.active)
    } else {
        stringResource(Res.string.blocked)
    }

    Row(
        modifier = modifier
            .wrapContentWidth()
            .background(
                color = animatedBackgroundColor,
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusDot(color = animatedContentColor)
        Text(
            text = statusText,
            style = Theme.typography.label.medium,
            softWrap = false,
            color = animatedContentColor
        )
    }
}

@Composable
private fun StatusDot(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(6.dp)
            .background(color = color, shape = CircleShape)
    )
}