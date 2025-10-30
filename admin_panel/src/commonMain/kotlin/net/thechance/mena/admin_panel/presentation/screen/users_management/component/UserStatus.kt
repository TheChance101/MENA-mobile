package net.thechance.mena.admin_panel.presentation.screen.users_management.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.domain.entity.UserStates
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
fun StatusManager(
    userStates: UserStates,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = userStates == UserStates.ACTIVE

    OutlinedButton(
        modifier = modifier.wrapContentWidth(),
        text = if (isActive) stringResource(Res.string.block) else stringResource(Res.string.activate),
        trailingIcon = painterResource(
            if (isActive) Res.drawable.ic_block else Res.drawable.ic_activate
        ),
        onClick = onClick
    )
}

@Composable
fun UserStatus(
    status: UserStates,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, dotColor, textColor, statusText) = when (status) {
        UserStates.ACTIVE -> StatusColors(
            backgroundColor = Theme.colorScheme.background.bgSuccess,
            dotColor = Theme.colorScheme.success,
            textColor = Theme.colorScheme.success,
            text = stringResource(Res.string.active)
        )

        UserStates.BLOCKED -> StatusColors(
            backgroundColor = Theme.colorScheme.background.bgError,
            dotColor = Theme.colorScheme.error,
            textColor = Theme.colorScheme.error,
            text = stringResource(Res.string.blocked)
        )
    }

    Row(
        modifier = modifier
            .wrapContentWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(100.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusDot(color = dotColor)
        Text(
            text = statusText,
            style = Theme.typography.label.medium,
            color = textColor
        )
    }
}

@Composable
private fun StatusDot(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.size(6.dp)
    ) {
        drawCircle(
            color = color,
            radius = size.minDimension / 2
        )
    }
}

private data class StatusColors(
    val backgroundColor: Color,
    val dotColor: Color,
    val textColor: Color,
    val text: String
)